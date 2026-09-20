package dev.mednikov.accounting.organizations.repositories;

import dev.mednikov.accounting.organizations.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByCountryAndTaxNumber (String country, String taxNumber);

}
