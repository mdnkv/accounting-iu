package dev.mednikov.accounting.currencies.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.mednikov.accounting.currencies.models.Currency;


@Repository
public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    Optional<Currency> findByCodeAndOrganizationId (String code, UUID organizationId);

    boolean existsByCodeAndOrganizationId (String code, UUID organizationId);

    List<Currency> findAllByOrganizationId(UUID organizationId);

    @Query("SELECT c FROM Currency c WHERE c.organization.id = :organizationId AND c.active = true")
    List<Currency> getActiveCurrencies(UUID organizationId);

    @Query("SELECT c FROM Currency c WHERE c.organization.id = :organizationId AND c.primary = true")
    Optional<Currency> getPrimaryCurrency(UUID organizationId);

}
