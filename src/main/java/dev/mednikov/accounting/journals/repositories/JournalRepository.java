package dev.mednikov.accounting.journals.repositories;

import dev.mednikov.accounting.journals.models.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {

    Optional<Journal> findByOrganizationIdAndName(UUID organizationId, String name);

    List<Journal> findAllByOrganizationId(UUID organizationId);

}
