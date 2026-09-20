package dev.mednikov.accounting.organizations.repositories;

import dev.mednikov.accounting.organizations.models.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, UUID> {

    @Query("SELECT ou FROM OrganizationUser ou WHERE ou.user.id = :userId AND ou.active=true")
    Optional<OrganizationUser> findActiveForUser (UUID userId);

    Optional<OrganizationUser> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    List<OrganizationUser> findAllByUserId(UUID userId);

    List<OrganizationUser> findAllByOrganizationId(UUID organizationId);

}
