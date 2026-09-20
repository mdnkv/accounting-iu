package dev.mednikov.accounting.transactions.repositories;

import dev.mednikov.accounting.transactions.models.TransactionLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionLineRepository extends JpaRepository<TransactionLine, UUID> {

    @Query("""
    SELECT tl FROM TransactionLine tl
    WHERE tl.transaction.organization.id = :organizationId
    AND tl.account.accountType IN ('ASSET', 'LIABILITY', 'EQUITY')
    AND tl.transaction.draft = false
    AND tl.transaction.date <= :toDate
    """)
    List<TransactionLine> getBalanceSheetLines (UUID organizationId, LocalDate toDate);

    @Query("""
    SELECT tl FROM TransactionLine tl
    WHERE tl.transaction.organization.id = :organizationId
    AND (tl.transaction.date >= :fromDate AND tl.transaction.date <= :toDate)
        AND tl.transaction.draft = false
    AND tl.account.accountType IN ('INCOME', 'EXPENSE')
    """)
    List<TransactionLine> getProfitLossLines(UUID organizationId, LocalDate fromDate, LocalDate toDate);

    @Query("""
    SELECT tl FROM TransactionLine tl
    WHERE tl.transaction.organization.id = :organizationId
    AND tl.account.accountType IN ('ASSET', 'LIABILITY')
    AND (tl.transaction.date >= :fromDate AND tl.transaction.date <= :toDate)
    AND tl.transaction.draft = false
    """)
    List<TransactionLine> getAssetsAndLiabilityLines (UUID organizationId, LocalDate fromDate, LocalDate toDate);

    @Query("""
    SELECT tl FROM TransactionLine tl
    WHERE tl.transaction.organization.id = :organizationId
    AND tl.account.accountType = 'EXPENSE'
    AND (tl.transaction.date >= :fromDate AND tl.transaction.date <= :toDate)
    AND tl.transaction.draft = false
    """)
    List<TransactionLine> getExpenseLines (UUID organizationId, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT tl FROM TransactionLine  tl WHERE tl.account.id = :accountId AND tl.transaction.draft = false")
    List<TransactionLine> findByAccountId(UUID accountId);

    @Query("SELECT tl FROM TransactionLine  tl WHERE tl.account.id = :accountId AND tl.transaction.date <= :date AND tl.transaction.draft = false")
    List<TransactionLine> findByAccountIdBeforeDate(UUID accountId, LocalDate date);
}
