package dev.mednikov.accounting.journals.services;

import dev.mednikov.accounting.journals.dto.JournalDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JournalService {

    JournalDto createJournal(JournalDto journalDto);

    JournalDto updateJournal(JournalDto journalDto);

    void deleteJournal (UUID journalId);

    List<JournalDto> getJournals (UUID organizationId);

    Optional<JournalDto> getJournal (UUID journalId);

}
