package dev.mednikov.accounting.transactions.repositories;

import dev.mednikov.accounting.transactions.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.organization.id = :organizationId ORDER BY t.date DESC")
    List<Transaction> findAllByOrganizationId (UUID organizationId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.baseCurrency.id = :currencyId OR t.targetCurrency.id = :currencyId")
    int countByCurrencyId (UUID currencyId);

}
