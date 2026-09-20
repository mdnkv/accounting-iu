package dev.mednikov.accounting.roles.dto;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;
import dev.mednikov.accounting.authorities.dto.AuthorityDtoMapper;
import dev.mednikov.accounting.roles.models.Role;

import java.util.List;
import java.util.function.Function;

public final class RoleDtoMapper implements Function<Role, RoleDto> {

    private final static AuthorityDtoMapper authorityDtoMapper = new AuthorityDtoMapper();

    @Override
    public RoleDto apply(Role role) {
        RoleDto result = new RoleDto();
        result.setName(role.getName());
        result.setId(role.getId());
        result.setOrganizationId(role.getOrganization().getId());
        // Map authorities to dto
        List<AuthorityDto> authorities = role.getAuthorities().stream().map(authorityDtoMapper).toList();
        result.setAuthorities(authorities);
        return result;
    }

}
