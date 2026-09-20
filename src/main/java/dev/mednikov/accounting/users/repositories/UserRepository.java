package dev.mednikov.accounting.users.repositories;

import dev.mednikov.accounting.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;
import java.util.UUID;

@ResponseBody
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakId (UUID id);

    Optional<User> findByEmail (String email);

}
