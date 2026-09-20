package dev.mednikov.accounting.users.services;

import dev.mednikov.accounting.users.events.UserCreatedEvent;
import dev.mednikov.accounting.users.models.User;
import dev.mednikov.accounting.users.repositories.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User getOrCreateUser(Jwt principal) {
        UUID keycloakId = UUID.fromString(principal.getSubject());
        Optional<User> result = this.userRepository.findByKeycloakId(keycloakId);

        if (result.isPresent()) {
            // Check that user was updated
            boolean updated = false;
            User user = result.get();
            if (!user.getFirstName().equals(principal.getClaim("given_name"))) {
                user.setFirstName(principal.getClaim("given_name"));
                updated = true;
            }
            if (!user.getLastName().equals(principal.getClaim("family_name"))){
                user.setLastName(principal.getClaim("family_name"));
                updated = true;
            }
            if (!user.getEmail().equals(principal.getClaim("email"))){
                user.setEmail(principal.getClaim("email"));
                updated = true;
            }
            if (!user.isEmailVerified() == principal.getClaimAsBoolean("email_verified")) {
                user.setEmailVerified(principal.getClaimAsBoolean("email_verified"));
                updated = true;
            }

            // Save user to db if was changed
            if (updated){
                return this.userRepository.save(user);
            } else {
                return user;
            }
        } else {
            // Create user
            User user = new User();
            user.setEmail(principal.getClaim("email"));
            user.setFirstName(principal.getClaim("given_name"));
            user.setLastName(principal.getClaim("family_name"));
            user.setKeycloakId(keycloakId);
            user.setPremium(false);
            user.setEmailVerified(principal.getClaimAsBoolean("email_verified"));
            user.setActive(true);
            user.setSuperuser(false);

            // todo set avatar
            user.setAvatarUrl("avatar");

            User savedUser = this.userRepository.save(user);

            // Also check for invitations
            UserCreatedEvent event = new UserCreatedEvent(this, savedUser);
            this.eventPublisher.publishEvent(event);

            return savedUser;
        }
    }

}
