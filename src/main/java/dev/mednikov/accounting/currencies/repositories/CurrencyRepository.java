package dev.mednikov.accounting.currencies.repositories;

import dev.mednikov.accounting.currencies.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    List<Currency> findAllByOrganizationId (UUID organizationId);

    Optional<Currency> findByCodeAndOrganizationId (String code, UUID organizationId);

    @Query("SELECT cu FROM Currency cu WHERE cu.organization.id = :organizationId AND cu.primary = true")
    Optional<Currency> findPrimaryCurrency (UUID organizationId);

}
