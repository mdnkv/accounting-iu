package dev.mednikov.accounting.organizations.repositories;

import dev.mednikov.accounting.organizations.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByEmailAndOrganizationId(String email, UUID organizationId);

    List<Invitation> findAllByEmail(String email);

}
