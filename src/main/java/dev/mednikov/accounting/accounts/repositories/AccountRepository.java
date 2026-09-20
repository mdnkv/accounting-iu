package dev.mednikov.accounting.accounts.repositories;

import dev.mednikov.accounting.accounts.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByOrganizationIdAndCode (UUID organizationId, String code);

    @Query("SELECT ac FROM Account ac WHERE ac.organization.id = :organizationId ORDER BY ac.code")
    List<Account> findAllByOrganizationId(UUID organizationId);

    @Query("SELECT ac FROM Account ac WHERE ac.organization.id = :organizationId AND ac.deprecated = false ORDER BY ac.code")
    List<Account> findActiveByOrganizationId(UUID organizationId);

    @Query("""
    SELECT ac FROM Account ac
    WHERE ac.organization.id = :organizationId
    AND ac.deprecated = false
    AND ac.accountType IN ('ASSET', 'LIABILITY', 'EQUITY')
    ORDER BY ac.code ASC
""")
    List<Account> findBalanceSheetAccounts (UUID organizationId);
}
