package dev.mednikov.accounting.authorities.services;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;

import java.util.List;
import java.util.UUID;

public interface AuthorityService {

    AuthorityDto createAuthority(AuthorityDto payload);

    AuthorityDto updateAuthority(AuthorityDto payload);

    void deleteAuthority (UUID id);

    List<AuthorityDto> getAuthorities(UUID organizationId);

}
