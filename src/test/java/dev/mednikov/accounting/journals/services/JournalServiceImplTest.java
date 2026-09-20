package dev.mednikov.accounting.journals.services;

import dev.mednikov.accounting.journals.dto.JournalDto;
import dev.mednikov.accounting.journals.exceptions.JournalAlreadyExistsException;
import dev.mednikov.accounting.journals.exceptions.JournalNotFoundException;
import dev.mednikov.accounting.journals.models.Journal;
import dev.mednikov.accounting.journals.repositories.JournalRepository;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class JournalServiceImplTest {

    @Mock private JournalRepository journalRepository;
    @Mock private OrganizationRepository organizationRepository;

    @InjectMocks private JournalServiceImpl journalService;

    @Test
    void createJournal_alreadyExistsTest(){
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Herbst Göbel e.G.");

        Journal journal = new Journal();
        journal.setOrganization(organization);
        journal.setName("Sales");

        JournalDto payload = new JournalDto();
        payload.setOrganizationId(organizationId);
        payload.setName("Sales");

        Mockito.when(journalRepository.findByOrganizationIdAndName(organizationId, "Sales")).thenReturn(Optional.of(journal));
        Assertions.assertThatThrownBy(() -> journalService.createJournal(payload)).isInstanceOf(JournalAlreadyExistsException.class);
    }

    @Test
    void createJournal_successTest(){
        UUID organizationId = UUID.randomUUID();
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Probst Stock GmbH & Co. KG");

        UUID journalId = UUID.randomUUID();
        Journal journal = new Journal();
        journal.setOrganization(organization);
        journal.setName("Sales");
        journal.setId(journalId);
        journal.setActive(true);

        JournalDto payload = new JournalDto();
        payload.setOrganizationId(organizationId);
        payload.setName("Sales");

        Mockito.when(journalRepository.findByOrganizationIdAndName(organizationId, "Sales")).thenReturn(Optional.empty());
        Mockito.when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        Mockito.when(journalRepository.save(journal)).thenReturn(journal);

        JournalDto result = journalService.createJournal(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void updateJournal_notFoundTest(){
        UUID journalId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();


        JournalDto payload = new JournalDto();
        payload.setId(journalId);
        payload.setOrganizationId(organizationId);
        payload.setName("Sales");
        payload.setActive(true);
        payload.setDescription("Aliquam iaculis ultricies quam sed sollicitudin.");

        Mockito.when(journalRepository.findById(journalId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> journalService.updateJournal(payload)).isInstanceOf(JournalNotFoundException.class);
    }

    @Test
    void updateJournal_successTest(){
        UUID journalId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Greiner Stiftung & Co. KG");

        Journal journal = new Journal();
        journal.setOrganization(organization);
        journal.setName("Sales");
        journal.setId(journalId);
        journal.setActive(true);
        journal.setDescription("Fusce scelerisque eros ut leo pellentesque rhoncus.");

        JournalDto payload = new JournalDto();
        payload.setId(journalId);
        payload.setOrganizationId(organizationId);
        payload.setName("Sales");
        payload.setActive(true);
        payload.setDescription("Fusce scelerisque eros ut leo pellentesque rhoncus.");

        Mockito.when(journalRepository.findById(journalId)).thenReturn(Optional.of(journal));
        Mockito.when(journalRepository.save(journal)).thenReturn(journal);

        JournalDto result = journalService.updateJournal(payload);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void updateJournal_nameAlreadyExistsTest(){
        UUID journalId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Schaller GmbH");

        Journal journal = new Journal();
        journal.setOrganization(organization);
        journal.setName("Sales");
        journal.setId(journalId);
        journal.setActive(true);

        JournalDto payload = new JournalDto();
        payload.setId(journalId);
        payload.setOrganizationId(organizationId);
        payload.setName("General");
        payload.setActive(true);

        Mockito.when(journalRepository.findById(journalId)).thenReturn(Optional.of(journal));
        Mockito.when(journalRepository.findByOrganizationIdAndName(organizationId, "General")).thenReturn(Optional.of(new Journal()));
        Assertions.assertThatThrownBy(() -> journalService.updateJournal(payload)).isInstanceOf(JournalAlreadyExistsException.class);
    }

    @Test
    void getJournal_existsTest(){
        UUID journalId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setName("Wilhelm Peters OH GmbH");

        Journal journal = new Journal();
        journal.setOrganization(organization);
        journal.setName("General");
        journal.setId(journalId);
        journal.setActive(true);
        journal.setDescription("Integer vel felis vitae purus facilisis venenatis");

        Mockito.when(journalRepository.findById(journalId)).thenReturn(Optional.of(journal));
        Optional<JournalDto> result = journalService.getJournal(journalId);
        Assertions.assertThat(result).isPresent();
    }

    @Test
    void getJournal_notExistsTest(){
        UUID journalId = UUID.randomUUID();
        Mockito.when(journalRepository.findById(journalId)).thenReturn(Optional.empty());
        Optional<JournalDto> result = journalService.getJournal(journalId);
        Assertions.assertThat(result).isEmpty();

    }

}
