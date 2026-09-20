package dev.mednikov.accounting.authorities.repositories;

import dev.mednikov.accounting.authorities.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

    Optional<Authority> findByOrganizationIdAndName(UUID organizationId, String name);

    List<Authority> findByOrganizationId(UUID organizationId);

}
