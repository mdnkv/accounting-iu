package dev.mednikov.accounting.users.services;

import dev.mednikov.accounting.users.models.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

    User getOrCreateUser (Jwt authPrincipal);

}
