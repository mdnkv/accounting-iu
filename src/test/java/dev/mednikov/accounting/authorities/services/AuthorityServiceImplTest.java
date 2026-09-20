package dev.mednikov.accounting.authorities.services;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;
import dev.mednikov.accounting.authorities.exceptions.AuthorityAlreadyExistsException;
import dev.mednikov.accounting.authorities.exceptions.AuthorityNotFoundException;
import dev.mednikov.accounting.authorities.models.Authority;
import dev.mednikov.accounting.authorities.repositories.AuthorityRepository;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
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
class AuthorityServiceImplTest {

    @Mock private AuthorityRepository authorityRepository;
    @Mock private OrganizationRepository organizationRepository;
    @InjectMocks private AuthorityServiceImpl authorityService;

    @Test
    void createAuthority_alreadyExistsTest(){
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Ebert Bruns Stiftung & Co. KG");

        String authorityName = "accounts:create";
        Authority authority = new Authority();
        authority.setOrganization(organization);
        authority.setId(UUID.randomUUID());
        authority.setName(authorityName);

        AuthorityDto payload = new AuthorityDto();
        payload.setOrganizationId(organizationId);
        payload.setName(authorityName);

        Mockito.when(authorityRepository.findByOrganizationIdAndName(organizationId, authorityName)).thenReturn(Optional.of(authority));

        Assertions.assertThatThrownBy(() -> authorityService.createAuthority(payload)).isInstanceOf(AuthorityAlreadyExistsException.class);
    }

    @Test
    void createAuthority_successTest(){
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Rupp GmbH & Co. KG");

        String authorityName = "accounts:create";
        Authority authority = new Authority();
        authority.setOrganization(organization);
        authority.setId(UUID.randomUUID());
        authority.setName(authorityName);

        AuthorityDto payload = new AuthorityDto();
        payload.setOrganizationId(organizationId);
        payload.setName(authorityName);

        Mockito.when(authorityRepository.findByOrganizationIdAndName(organizationId, authorityName)).thenReturn(Optional.empty());
        Mockito.when(organizationRepository.getReferenceById(organizationId)).thenReturn(organization);
        Mockito.when(authorityRepository.save(authority)).thenReturn(authority);

        AuthorityDto result = authorityService.createAuthority(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void updateAuthority_notFoundTest(){
        UUID organizationId = UUID.randomUUID();
        UUID authorityId = UUID.randomUUID();

        String authorityName = "accounts:update";
        AuthorityDto payload = new AuthorityDto();
        payload.setOrganizationId(organizationId);
        payload.setName(authorityName);
        payload.setId(authorityId);

        Mockito.when(authorityRepository.findById(authorityId)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> authorityService.updateAuthority(payload)).isInstanceOf(AuthorityNotFoundException.class);
    }

    @Test
    void updateAuthority_alreadyExistsTest(){
        UUID organizationId = UUID.randomUUID();
        UUID authorityId = UUID.randomUUID();

        String authorityName = "accounts:update";

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Schott GmbH");

        Authority authority = new Authority();
        authority.setOrganization(organization);
        authority.setId(UUID.randomUUID());
        authority.setName(authorityName);
        authority.setId(authorityId);

        AuthorityDto payload = new AuthorityDto();
        payload.setOrganizationId(organizationId);
        payload.setName("transactions:create");
        payload.setId(authorityId);

        Mockito.when(authorityRepository.findById(authorityId)).thenReturn(Optional.of(authority));
        Mockito.when(authorityRepository.findByOrganizationIdAndName(organizationId, "transactions:create")).thenReturn(Optional.of(new Authority()));
        Assertions.assertThatThrownBy(() -> authorityService.updateAuthority(payload)).isInstanceOf(AuthorityAlreadyExistsException.class);
    }

    @Test
    void updateAuthority_successTest(){
        UUID organizationId = UUID.randomUUID();
        UUID authorityId = UUID.randomUUID();

        String authorityName = "accounts:update";

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Kluge Engelmann AG & Co. KGaA");

        Authority authority = new Authority();
        authority.setOrganization(organization);
        authority.setId(UUID.randomUUID());
        authority.setName(authorityName);
        authority.setId(authorityId);

        AuthorityDto payload = new AuthorityDto();
        payload.setOrganizationId(organizationId);
        payload.setName(authorityName);
        payload.setId(authorityId);

        Mockito.when(authorityRepository.findById(authorityId)).thenReturn(Optional.of(authority));
        Mockito.when(authorityRepository.save(authority)).thenReturn(authority);

        AuthorityDto result = authorityService.updateAuthority(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void getAuthoritiesTest(){
        UUID organizationId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Stein Rothe GmbH");

        Authority authority = new Authority();
        authority.setOrganization(organization);
        authority.setId(UUID.randomUUID());
        authority.setName("transactions:create");
        authority.setId(UUID.randomUUID());

        List<Authority> authorities = List.of(authority);

        Mockito.when(authorityRepository.findByOrganizationId(organizationId)).thenReturn(authorities);
        List<AuthorityDto> result = authorityService.getAuthorities(organizationId);
        Assertions.assertThat(result).isNotNull().hasSize(1);

    }

}
