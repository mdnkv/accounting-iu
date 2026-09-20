package dev.mednikov.accounting.journals.services;

import dev.mednikov.accounting.journals.dto.JournalDto;
import dev.mednikov.accounting.journals.dto.JournalDtoMapper;
import dev.mednikov.accounting.journals.exceptions.JournalAlreadyExistsException;
import dev.mednikov.accounting.journals.exceptions.JournalNotFoundException;
import dev.mednikov.accounting.journals.models.Journal;
import dev.mednikov.accounting.journals.repositories.JournalRepository;
import dev.mednikov.accounting.organizations.exceptions.OrganizationNotFoundException;
import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.organizations.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class JournalServiceImpl implements JournalService {

    private final static JournalDtoMapper journalDtoMapper = new JournalDtoMapper();

    private final JournalRepository journalRepository;
    private final OrganizationRepository organizationRepository;

    public JournalServiceImpl(JournalRepository journalRepository, OrganizationRepository organizationRepository) {
        this.journalRepository = journalRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public JournalDto createJournal(JournalDto payload) {
        if (this.journalRepository.findByOrganizationIdAndName(payload.getOrganizationId(), payload.getName()).isPresent()) {
            throw new JournalAlreadyExistsException();
        }

        Organization organization = this.organizationRepository.findById(payload.getOrganizationId()).orElseThrow(OrganizationNotFoundException::new);
        Journal journal = new Journal();
        journal.setName(payload.getName());
        journal.setOrganization(organization);
        journal.setDescription(payload.getDescription());
        journal.setActive(true);

        Journal result = this.journalRepository.save(journal);

        return journalDtoMapper.apply(result);
    }

    @Override
    public JournalDto updateJournal(JournalDto payload) {
        Objects.requireNonNull(payload.getId());
        Journal journal = this.journalRepository.findById(payload.getId()).orElseThrow(JournalNotFoundException::new);

        // verify that the name is not in use
        if (!journal.getName().equals(payload.getName())) {
            if (this.journalRepository.findByOrganizationIdAndName(payload.getOrganizationId(), payload.getName()).isPresent()) {
                throw new JournalAlreadyExistsException();
            }
            journal.setName(payload.getName());
        }
        journal.setDescription(payload.getDescription());
        journal.setActive(payload.isActive());
        Journal result = this.journalRepository.save(journal);

        return journalDtoMapper.apply(result);
    }

    @Override
    public void deleteJournal(UUID journalId) {
        this.journalRepository.deleteById(journalId);
    }

    @Override
    public List<JournalDto> getJournals(UUID organizationId) {
        return this.journalRepository.findAllByOrganizationId(organizationId)
                .stream()
                .map(journalDtoMapper)
                .toList();
    }

    @Override
    public Optional<JournalDto> getJournal(UUID journalId) {
        return this.journalRepository.findById(journalId).map(journalDtoMapper);
    }
}
