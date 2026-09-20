package dev.mednikov.accounting.shared.auth;

import dev.mednikov.accounting.authorities.models.Authority;
import dev.mednikov.accounting.organizations.models.OrganizationUser;
import dev.mednikov.accounting.organizations.repositories.OrganizationUserRepository;
import dev.mednikov.accounting.users.models.User;
import dev.mednikov.accounting.users.services.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile({"dev", "prod"})
public class RoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;
    private final OrganizationUserRepository organizationUserRepository;

    public RoleConverter(UserService userService, OrganizationUserRepository organizationUserRepository) {
        this.userService = userService;
        this.organizationUserRepository = organizationUserRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        User user = this.userService.getOrCreateUser(jwt);
        Optional<OrganizationUser> currentActive = this.organizationUserRepository.findActiveForUser(user.getId());
        if (currentActive.isPresent()) {
            OrganizationUser result = currentActive.get();
            UUID organizationId = result.getOrganization().getId();
            // Map authorities to Spring GrantedAuthority objects
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (Authority authority: result.getRole().getAuthorities()) {
                authorities.add(new SimpleGrantedAuthority(authority.getName()));
            }
            authorities.add(new SimpleGrantedAuthority(organizationId.toString()));
            return new JwtAuthenticationToken(jwt, authorities);
        } else {
            // no active role is presented
            return new JwtAuthenticationToken(jwt, List.of());
        }

    }
}
