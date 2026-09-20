package dev.mednikov.accounting.organizations.services;

import dev.mednikov.accounting.organizations.dto.OrganizationDto;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import dev.mednikov.accounting.users.models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private OrganizationRepository organizationRepository;
    @InjectMocks private OrganizationServiceImpl organizationService;

    @Test
    void createOrganizationTest(){
        UUID organizationId = UUID.randomUUID();
        OrganizationDto payload = new OrganizationDto();
        payload.setName("Wilhelm Meißner AG");

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Wilhelm Meißner AG");

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setKeycloakId(UUID.randomUUID());
        user.setEmail("vy7e5804yikk6gcq33ht@ymail.com");

        Mockito.when(organizationRepository.save(organization)).thenReturn(organization);

        OrganizationDto result = organizationService.createOrganization(user, payload);
        Mockito.verify(organizationRepository, Mockito.times(1)).save(organization);
        Assertions.assertThat(result).isNotNull();

    }

    @Test
    void updateOrganization_notFoundTest(){
        UUID organizationId = UUID.randomUUID();
        OrganizationDto payload = new OrganizationDto();
        payload.setName("Sturm Kessler GmbH");
        payload.setId(organizationId);

        Mockito.when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());
        Assertions
                .assertThatThrownBy(() -> organizationService.updateOrganization(payload))
                .isInstanceOf(OrganizationNotFoundException.class);
    }

    @Test
    void updateOrganization_successTest(){
        UUID organizationId = UUID.randomUUID();

        OrganizationDto payload = new OrganizationDto();
        payload.setName("Hanke Stiftung & Co. KG");
        payload.setId(organizationId);

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Hanke Stiftung & Co. KG");

        Mockito.when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        Mockito.when(organizationRepository.save(organization)).thenReturn(organization);

        OrganizationDto result = organizationService.updateOrganization(payload);
        Mockito.verify(organizationRepository, Mockito.times(1)).save(organization);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void getOrganizationById_existsTest(){
        UUID organizationId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Gärtner GmbH & Co. KG");

        Mockito.when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));

        Optional<OrganizationDto> result = organizationService.getOrganization(organizationId);
        Assertions.assertThat(result).isPresent();
    }

    @Test
    void getOrganizationById_doesNotExistTest(){
        UUID organizationId = UUID.randomUUID();
        Mockito.when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());
        Optional<OrganizationDto> result = organizationService.getOrganization(organizationId);
        Assertions.assertThat(result).isEmpty();
    }

}
