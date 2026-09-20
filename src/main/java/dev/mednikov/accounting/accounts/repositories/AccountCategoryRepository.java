package dev.mednikov.accounting.accounts.repositories;

import dev.mednikov.accounting.accounts.models.AccountCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountCategoryRepository extends JpaRepository<AccountCategory, UUID> {

    List<AccountCategory> findByOrganizationId(UUID organizationId);

    Optional<AccountCategory> findByOrganizationIdAndName(UUID organizationId, String name);

}
