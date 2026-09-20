package dev.mednikov.accounting.journals.dto;

import dev.mednikov.accounting.journals.models.Journal;

import java.util.function.Function;

public final class JournalDtoMapper implements Function<Journal, JournalDto> {

    @Override
    public JournalDto apply(Journal journal) {
        JournalDto result = new JournalDto();
        result.setId(journal.getId());
        result.setName(journal.getName());
        result.setDescription(journal.getDescription());
        result.setActive(journal.isActive());
        result.setOrganizationId(journal.getOrganization().getId());
        return result;
    }

}
