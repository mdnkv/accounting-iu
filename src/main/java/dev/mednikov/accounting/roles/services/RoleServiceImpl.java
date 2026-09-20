package dev.mednikov.accounting.roles.services;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;
import dev.mednikov.accounting.authorities.exceptions.AuthorityNotFoundException;
import dev.mednikov.accounting.authorities.models.Authority;
import dev.mednikov.accounting.authorities.repositories.AuthorityRepository;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.roles.dto.RoleDto;
import dev.mednikov.accounting.roles.dto.RoleDtoMapper;
import dev.mednikov.accounting.roles.exceptions.RoleAlreadyExistsException;
import dev.mednikov.accounting.roles.exceptions.RoleNotFoundException;
import dev.mednikov.accounting.roles.models.Role;
import dev.mednikov.accounting.roles.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final static RoleDtoMapper mapper = new RoleDtoMapper();

    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final AuthorityRepository authorityRepository;

    public RoleServiceImpl(RoleRepository roleRepository, OrganizationRepository organizationRepository, AuthorityRepository authorityRepository) {
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public RoleDto createRole(RoleDto payload) {
        String name = payload.getName();

        if (this.roleRepository.findByNameAndOrganizationId(name, payload.getOrganizationId()).isPresent()) {
            throw new RoleAlreadyExistsException();
        }

//        Organization organization = this.organizationRepository.getReferenceById(pa);
        Organization organization = this.organizationRepository.findById(payload.getOrganizationId()).
                orElseThrow(() -> new OrganizationNotFoundException());

        Role role = new Role();
        role.setName(name);
        role.setOrganization(organization);

        // Set authorities
        if (!payload.getAuthorities().isEmpty()){
            List<UUID> authoritiesIds = payload.getAuthorities()
                    .stream()
                    .map(AuthorityDto::getId)
                    .toList();
            List<Authority> authorities = this.authorityRepository.findAllById(authoritiesIds);
            role.setAuthorities(new HashSet<>(authorities));
        }

        Role result = this.roleRepository.save(role);
        return mapper.apply(result);
    }

    @Override
    public RoleDto updateRole(RoleDto payload) {
        Objects.requireNonNull(payload.getId());

        Role role = this.roleRepository.findById(payload.getId()).orElseThrow(RoleNotFoundException::new);
        if (!role.getName().equals(payload.getName())) {
            // name is changed, need to verify that no other role is present
            if (this.roleRepository.findByNameAndOrganizationId(payload.getName(), payload.getOrganizationId()).isPresent()) {
                throw new RoleAlreadyExistsException();
            }
        }

        role.setName(payload.getName());
        Role result = this.roleRepository.save(role);

        return mapper.apply(result);
    }

    @Override
    public void deleteRole(UUID id) {
        this.roleRepository.deleteById(id);
    }

    @Override
    public List<RoleDto> getRoles(UUID organizationId) {
        return this.roleRepository.findByOrganizationId(organizationId)
                .stream()
                .map(mapper)
                .toList();
    }

    @Override
    public void addAuthorityToRole(UUID roleId, UUID authorityId) {
        Role role = this.roleRepository.findById(roleId).orElseThrow(RoleNotFoundException::new);
        Authority authority = this.authorityRepository.findById(authorityId).orElseThrow(AuthorityNotFoundException::new);
        boolean result = role.getAuthorities().add(authority);
        if (result){
            this.roleRepository.save(role);
            this.authorityRepository.save(authority);
        }
    }

    @Override
    public void removeAuthorityFromRole(UUID roleId, UUID authorityId) {
        Role role = this.roleRepository.findById(roleId).orElseThrow(RoleNotFoundException::new);
        Authority authority = this.authorityRepository.findById(authorityId).orElseThrow(AuthorityNotFoundException::new);
        boolean deleted = role.getAuthorities().remove(authority);
        if (deleted){
            this.roleRepository.save(role);
            this.authorityRepository.save(authority);
        }
    }
}
