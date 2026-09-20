package dev.mednikov.accounting.organizations.services;

import dev.mednikov.accounting.organizations.dto.*;
import dev.mednikov.accounting.organizations.events.CreateOwnerEvent;
import dev.mednikov.accounting.organizations.exceptions.OrganizationUserAlreadyExistsException;
import dev.mednikov.accounting.organizations.exceptions.OrganizationUserNotFoundException;
import dev.mednikov.accounting.organizations.models.Invitation;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.models.OrganizationUser;
import dev.mednikov.accounting.organizations.repositories.InvitationRepository;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.organizations.repositories.OrganizationUserRepository;
import dev.mednikov.accounting.roles.exceptions.RoleNotFoundException;
import dev.mednikov.accounting.roles.models.Role;
import dev.mednikov.accounting.roles.repositories.RoleRepository;
import dev.mednikov.accounting.users.events.UserCreatedEvent;
import dev.mednikov.accounting.users.models.User;
import dev.mednikov.accounting.users.repositories.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrganizationUserServiceImpl implements OrganizationUserService {

    private final static OrganizationUserDtoMapper organizationUserDtoMapper = new OrganizationUserDtoMapper();
    private final static UserOrganizationDtoMapper userOrganizationDtoMapper = new UserOrganizationDtoMapper();
//    private final static SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationUserRepository organizationUserRepository;

    public OrganizationUserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            OrganizationRepository organizationRepository,
            OrganizationUserRepository organizationUserRepository,
            InvitationRepository invitationRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.organizationUserRepository = organizationUserRepository;
        this.invitationRepository = invitationRepository;
    }

    @Override
    public Optional<UserOrganizationDto> getActiveForUser(User user) {
        return this.organizationUserRepository.findActiveForUser(user.getId()).map(userOrganizationDtoMapper);
    }

    @Override
    public List<UserOrganizationDto> getAllForUser(User user) {
        return this.organizationUserRepository.findAllByUserId(user.getId()).stream().map(userOrganizationDtoMapper).toList();
    }

    @Override
    public UserOrganizationDto setActiveForUser(User user, UUID id) {
        // Find current active
        Optional<OrganizationUser> currentActive = this.organizationUserRepository.findActiveForUser(user.getId());
        // Set current active as not active
        if (currentActive.isPresent()) {
            OrganizationUser current = currentActive.get();
            if (current.getId().equals(id)) {
                // User just misuses API and wants a current role to be active,
                // then we just return it
                return userOrganizationDtoMapper.apply(current);
            }
            current.setActive(false);
            this.organizationUserRepository.save(current);
        }
        // Find organization user by id
        OrganizationUser organizationUser = this.organizationUserRepository.findById(id).orElseThrow(OrganizationUserNotFoundException::new);
        // Set as active
        organizationUser.setActive(true);
        // Return result
        OrganizationUser result = this.organizationUserRepository.save(organizationUser);
        return userOrganizationDtoMapper.apply(result);
    }

    @Override
    public Optional<OrganizationUserDto> createOrganizationUser(CreateOrganizationUserRequestDto payload) {
        // Find user by email
        String email = payload.getEmail();
//        Long organizationId = Long.parseLong(payload.getOrganizationId());
        UUID organizationId = payload.getOrganizationId();
        // Get a role
//        Long roleId = Long.parseLong(payload.getRoleId());
        UUID roleId = payload.getRoleId();
        Role role = this.roleRepository.findById(roleId).orElseThrow(RoleNotFoundException::new);
        // Get an organization
        Organization organization = this.organizationRepository.getReferenceById(organizationId);

        Optional<User> userResult = this.userRepository.findByEmail(email);
        if (userResult.isPresent()) {
            // Option 1: Create an organization user
            User user = userResult.get();
            // Check that the user does not exist in the organization yet
            if (this.organizationUserRepository.findByOrganizationIdAndUserId(organizationId, user.getId()).isPresent()) {
                throw new OrganizationUserAlreadyExistsException();
            }
            // Save
            boolean active = this.organizationUserRepository.findAllByUserId(user.getId()).isEmpty();
            OrganizationUser organizationUser = new OrganizationUser();
            organizationUser.setOrganization(organization);
            organizationUser.setRole(role);
            organizationUser.setUser(user);
//            organizationUser.setId(snowflakeGenerator.next());
            organizationUser.setActive(active);

            OrganizationUser result = this.organizationUserRepository.save(organizationUser);
            OrganizationUserDto dto = organizationUserDtoMapper.apply(result);
            return Optional.of(dto);
        } else {
            // Option 2: Create invitation
            if (this.invitationRepository.findByEmailAndOrganizationId(email, organizationId).isEmpty()){
                // create an invitation and save it
                Invitation invitation = new Invitation();
                invitation.setEmail(email);
                invitation.setOrganization(organization);
                invitation.setRole(role);
//                invitation.setId(snowflakeGenerator.next());
                this.invitationRepository.save(invitation);
            }
            return Optional.empty();
        }
    }

    @Override
    public OrganizationUserDto updateOrganizationUser(OrganizationUserDto payload) {
        Objects.requireNonNull(payload.getId());
        OrganizationUser organizationUser = this.organizationUserRepository.findById(payload.getId()).orElseThrow(OrganizationUserNotFoundException::new);
        Role role = this.roleRepository.findById(payload.getRoleId()).orElseThrow(RoleNotFoundException::new);
        organizationUser.setRole(role);
        OrganizationUser result = this.organizationUserRepository.save(organizationUser);
        return organizationUserDtoMapper.apply(result);
    }

    @Override
    public void deleteOrganizationUser(UUID id) {
        this.organizationUserRepository.deleteById(id);
    }

    @Override
    public List<OrganizationUserDto> getUsersInOrganization(UUID organizationId) {
        return this.organizationUserRepository
                .findAllByOrganizationId(organizationId)
                .stream()
                .map(organizationUserDtoMapper)
                .toList();
    }

    @EventListener
    public void onCreateOwnerEventListener (CreateOwnerEvent event) {
        User user = event.getUser();
        // Check if the user has other roles
        boolean active = this.organizationUserRepository.findAllByUserId(user.getId()).isEmpty();

        // Create an organization user entity
        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setActive(active);
        organizationUser.setUser(user);
        organizationUser.setRole(event.getRole());
        organizationUser.setOrganization(event.getOrganization());
        this.organizationUserRepository.save(organizationUser);
    }

    @EventListener
    public void onUserCreatedEventListener (UserCreatedEvent event) {
        User user = event.getUser();
        List<Invitation> invitations = this.invitationRepository.findAllByEmail(user.getEmail());
        if (!invitations.isEmpty()) {
            // Convert invitations to organization user
            List<OrganizationUser> organizationUsers = new ArrayList<>();
            for (int i = 0; i < invitations.size(); i++) {
                Invitation invitation = invitations.get(i);
                Role role = invitation.getRole();
                Organization organization = invitation.getOrganization();
                boolean active = i == 0;
                OrganizationUser organizationUser = new OrganizationUser();
                organizationUser.setRole(role);
                organizationUser.setOrganization(organization);
                organizationUser.setUser(user);
                organizationUser.setActive(active);
                organizationUsers.add(organizationUser);
            }
            this.organizationUserRepository.saveAll(organizationUsers);
            this.invitationRepository.deleteAll(invitations);
        }
    }

}
