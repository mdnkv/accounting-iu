package dev.mednikov.accounting.authorities.dto;

import dev.mednikov.accounting.authorities.models.Authority;

import java.util.function.Function;

public final class AuthorityDtoMapper implements Function<Authority, AuthorityDto> {

    @Override
    public AuthorityDto apply(Authority authority) {
        AuthorityDto result = new AuthorityDto();
        result.setName(authority.getName());
        result.setId(authority.getId());
        result.setOrganizationId(authority.getOrganization().getId());
        return result;
    }

}
