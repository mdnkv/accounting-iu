package dev.mednikov.accounting.roles.services;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;
import dev.mednikov.accounting.authorities.repositories.AuthorityRepository;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.roles.dto.RoleDto;
import dev.mednikov.accounting.roles.exceptions.RoleAlreadyExistsException;
import dev.mednikov.accounting.roles.exceptions.RoleNotFoundException;
import dev.mednikov.accounting.roles.models.Role;
import dev.mednikov.accounting.roles.repositories.RoleRepository;
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
class RoleServiceImplTest {

    @Mock private RoleRepository roleRepository;
    @Mock private AuthorityRepository authorityRepository;
    @Mock private OrganizationRepository organizationRepository;
    @InjectMocks private RoleServiceImpl roleService;

    @Test
    void createRole_alreadyExistsTest(){
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Rupp Moser GmbH");

        Role role = new Role();
        role.setId(roleId);
        role.setName("Administrator");
        role.setOrganization(organization);

        AuthorityDto authorityDto = new AuthorityDto();
        authorityDto.setId(UUID.randomUUID());
        authorityDto.setName("accounts:create");
        authorityDto.setOrganizationId(organizationId);
        RoleDto payload = new RoleDto();
        payload.setAuthorities(List.of(authorityDto));
        payload.setOrganizationId(organizationId);
        payload.setName("Administrator");

        Mockito.when(roleRepository.findByNameAndOrganizationId("Administrator", organizationId)).thenReturn(Optional.of(role));
        Assertions.assertThatThrownBy(() -> roleService.createRole(payload)).isInstanceOf(RoleAlreadyExistsException.class);
    }

    @Test
    void createRole_successTest(){
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Neuhaus Schultz e.G.");

        Role role = new Role();
        role.setId(roleId);
        role.setName("Administrator");
        role.setOrganization(organization);

        RoleDto payload = new RoleDto();
        payload.setOrganizationId(organizationId);
        payload.setName("Administrator");
        payload.setAuthorities(List.of());

        Mockito.when(roleRepository.findByNameAndOrganizationId("Administrator", organizationId)).thenReturn(Optional.empty());
        Mockito.when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        RoleDto result = roleService.createRole(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void updateRole_alreadyExistsTest(){
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Fuhrmann e.V.");

        Role role = new Role();
        role.setId(roleId);
        role.setName("Administrator");
        role.setOrganization(organization);

        RoleDto payload = new RoleDto();
        payload.setId(roleId);
        payload.setOrganizationId(organizationId);
        payload.setName("User");
        payload.setAuthorities(List.of());

        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Mockito.when(roleRepository.findByNameAndOrganizationId("User", organizationId)).thenReturn(Optional.of(new Role()));
        Assertions.assertThatThrownBy(() -> roleService.updateRole(payload)).isInstanceOf(RoleAlreadyExistsException.class);
    }

    @Test
    void updateRole_notFoundTest(){
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        RoleDto payload = new RoleDto();
        payload.setId(roleId);
        payload.setOrganizationId(organizationId);
        payload.setName("User");
        payload.setAuthorities(List.of());

        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> roleService.updateRole(payload)).isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void updateRole_successTest(){
        UUID organizationId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Bode Hauser AG");

        Role role = new Role();
        role.setId(roleId);
        role.setName("Administrator");
        role.setOrganization(organization);

        RoleDto payload = new RoleDto();
        payload.setId(roleId);
        payload.setOrganizationId(organizationId);
        payload.setName("User");
        payload.setAuthorities(List.of());

        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        Mockito.when(roleRepository.findByNameAndOrganizationId("User", organizationId)).thenReturn(Optional.empty());
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        RoleDto result = roleService.updateRole(payload);
        Assertions.assertThat(result).isNotNull();
    }

}
