package dev.mednikov.accounting.organizations.services;

import dev.mednikov.accounting.organizations.dto.CreateOrganizationUserRequestDto;
import dev.mednikov.accounting.organizations.dto.OrganizationUserDto;
import dev.mednikov.accounting.organizations.dto.OrganizationUserDtoMapper;
import dev.mednikov.accounting.organizations.dto.UserOrganizationDto;
import dev.mednikov.accounting.organizations.exceptions.OrganizationUserAlreadyExistsException;
import dev.mednikov.accounting.organizations.models.Invitation;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.models.OrganizationUser;
import dev.mednikov.accounting.organizations.repositories.InvitationRepository;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.organizations.repositories.OrganizationUserRepository;
import dev.mednikov.accounting.roles.models.Role;
import dev.mednikov.accounting.roles.repositories.RoleRepository;
import dev.mednikov.accounting.users.models.User;
import dev.mednikov.accounting.users.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrganizationUserServiceImplTest {

    private final static OrganizationUserDtoMapper organizationUserDtoMapper = new OrganizationUserDtoMapper();

    @Mock private OrganizationUserRepository organizationUserRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRepository userRepository;
    @Mock private InvitationRepository invitationRepository;
    @InjectMocks private OrganizationUserServiceImpl organizationUserService;

    @Test
    void getActiveForUser_existsTest(){
        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Rapp Schramm GmbH & Co. KG");

        Role role = new Role();
        role.setId(roleId);
        role.setOrganization(organization);
        role.setName("User");

        User user = new User();
        user.setId(userId);
        user.setEmail("albrecht.horstdieter@ritter.org");
        user.setFirstName("Jutta");
        user.setLastName("Fröhlich");
        user.setKeycloakId(UUID.randomUUID());

        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setOrganization(organization);
        organizationUser.setRole(role);
        organizationUser.setUser(user);
        organizationUser.setActive(true);
        organizationUser.setId(UUID.randomUUID());

        Mockito.when(organizationUserRepository.findActiveForUser(userId)).thenReturn(Optional.of(organizationUser));
        Optional<UserOrganizationDto> result = organizationUserService.getActiveForUser(user);
        Assertions.assertThat(result).isPresent();
    }

    @Test
    void getActiveForUser_notExistsTest(){
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("ekkehard.jakob@fiedler.com");
        user.setFirstName("Katja");
        user.setLastName("Held-Schröter");
        user.setKeycloakId(UUID.randomUUID());

        Mockito.when(organizationUserRepository.findActiveForUser(userId)).thenReturn(Optional.empty());
        Optional<UserOrganizationDto> result = organizationUserService.getActiveForUser(user);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void setActiveForUser_successTest(){
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID organizationUserId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Albrecht Böhme GmbH & Co. KGaA");

        User user = new User();
        user.setId(userId);
        user.setEmail("gudrun32@lehmann.de");
        user.setFirstName("Kunigunde");
        user.setLastName("Fleischer-Wahl");
        user.setKeycloakId(UUID.randomUUID());

        Role role = new Role();
        role.setId(roleId);
        role.setOrganization(organization);
        role.setName("User");

        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setOrganization(organization);
        organizationUser.setRole(role);
        organizationUser.setUser(user);
        organizationUser.setActive(true);
        organizationUser.setId(organizationUserId);

        Mockito.when(organizationUserRepository.findActiveForUser(userId)).thenReturn(Optional.empty());
        Mockito.when(organizationUserRepository.findById(organizationUserId)).thenReturn(Optional.of(organizationUser));
        Mockito.when(organizationUserRepository.save(organizationUser)).thenReturn(organizationUser);

        UserOrganizationDto result = organizationUserService.setActiveForUser(user, organizationUserId);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void createOrganizationUser_alreadyExistsTest(){
        String email = "wolfdieter69@schuler.com";
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Brückner Greiner AG");

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFirstName("Leonore");
        user.setLastName("Ritter");

        Role role = new Role();
        role.setId(roleId);
        role.setOrganization(organization);
        role.setName("User");

        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setOrganization(organization);
        organizationUser.setRole(role);
        organizationUser.setUser(user);
        organizationUser.setActive(true);
        organizationUser.setId(UUID.randomUUID());

        CreateOrganizationUserRequestDto payload = new CreateOrganizationUserRequestDto();
        payload.setEmail(email);
        payload.setOrganizationId(organizationId);
        payload.setRoleId(roleId);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Mockito.when(organizationUserRepository.findByOrganizationIdAndUserId(organizationId, userId)).thenReturn(Optional.of(organizationUser));
        Assertions.assertThatThrownBy(() -> organizationUserService.createOrganizationUser(payload)).isInstanceOf(OrganizationUserAlreadyExistsException.class);

    }

    @Test
    void createOrganizationUser_successTest(){
        String email = "annika.altmann@bergmann.com";
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Zander GmbH & Co. KG");

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFirstName("Jacqueline");
        user.setLastName("Weiss");

        Role role = new Role();
        role.setId(roleId);
        role.setOrganization(organization);
        role.setName("User");

        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setOrganization(organization);
        organizationUser.setRole(role);
        organizationUser.setUser(user);
        organizationUser.setActive(true);
        organizationUser.setId(UUID.randomUUID());

        CreateOrganizationUserRequestDto payload = new CreateOrganizationUserRequestDto();
        payload.setEmail(email);
        payload.setOrganizationId(organizationId);
        payload.setRoleId(roleId);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(organizationUserRepository.findByOrganizationIdAndUserId(organizationId, userId)).thenReturn(Optional.empty());
        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Mockito.when(organizationRepository.getReferenceById(organizationId)).thenReturn(organization);
        Mockito.when(organizationUserRepository.findAllByUserId(userId)).thenReturn(List.of());
        Mockito.when(organizationUserRepository.save(Mockito.any())).thenReturn(organizationUser);

        Optional<OrganizationUserDto> result = organizationUserService.createOrganizationUser(payload);
        Assertions.assertThat(result).isPresent();
    }

    @Test
    void createOrganizationUser_withInvitationTest(){
        String email = "dietrich.silvana@hanke.de";
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Eberhardt Bischoff GmbH & Co. KG");

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFirstName("Rebecca");
        user.setLastName("Böhm");

        Role role = new Role();
        role.setId(roleId);
        role.setOrganization(organization);
        role.setName("User");

        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setRole(role);
        invitation.setOrganization(organization);
        invitation.setId(UUID.randomUUID());

        CreateOrganizationUserRequestDto payload = new CreateOrganizationUserRequestDto();
        payload.setEmail(email);
        payload.setOrganizationId(organizationId);
        payload.setRoleId(roleId);

        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Mockito.when(organizationRepository.getReferenceById(organizationId)).thenReturn(organization);
        Mockito.when(invitationRepository.findByEmailAndOrganizationId(email, organizationId)).thenReturn(Optional.empty());
        Mockito.when(invitationRepository.save(Mockito.any())).thenReturn(invitation);

        Optional<OrganizationUserDto> result = organizationUserService.createOrganizationUser(payload);
        Assertions.assertThat(result).isEmpty();

    }

    @Test
    void updateOrganizationUserTest(){
        String email = "adler.kathrin@fleischmann.de";
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID organizationUserId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Heine GmbH");

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFirstName("Jeannette");
        user.setLastName("Meyer");

        Role role = new Role();
        role.setId(roleId);
        role.setOrganization(organization);
        role.setName("User");

        OrganizationUser organizationUser = new OrganizationUser();
        organizationUser.setOrganization(organization);
        organizationUser.setRole(role);
        organizationUser.setUser(user);
        organizationUser.setActive(true);
        organizationUser.setId(organizationUserId);

        OrganizationUserDto payload = organizationUserDtoMapper.apply(organizationUser);

        Mockito.when(organizationUserRepository.findById(organizationUserId)).thenReturn(Optional.of(organizationUser));
        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Mockito.when(organizationUserRepository.save(Mockito.any())).thenReturn(organizationUser);

        OrganizationUserDto result = organizationUserService.updateOrganizationUser(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void getUsersInOrganizationTest(){
        UUID organizationId = UUID.randomUUID();
        Mockito.when(organizationUserRepository.findAllByOrganizationId(organizationId)).thenReturn(List.of());
        List<OrganizationUserDto> result = organizationUserService.getUsersInOrganization(organizationId);
        Assertions.assertThat(result).hasSize(0);
    }

}
