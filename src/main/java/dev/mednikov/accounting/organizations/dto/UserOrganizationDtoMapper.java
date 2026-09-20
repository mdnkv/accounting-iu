package dev.mednikov.accounting.organizations.dto;

import dev.mednikov.accounting.organizations.models.OrganizationUser;
import dev.mednikov.accounting.roles.dto.RoleDto;
import dev.mednikov.accounting.roles.dto.RoleDtoMapper;

import java.util.function.Function;

public final class UserOrganizationDtoMapper implements Function<OrganizationUser, UserOrganizationDto> {

    private final static RoleDtoMapper roleDtoMapper = new RoleDtoMapper();
    private final static OrganizationDtoMapper organizationDtoMapper = new OrganizationDtoMapper();

    @Override
    public UserOrganizationDto apply(OrganizationUser organizationUser) {
        UserOrganizationDto result = new UserOrganizationDto();
        RoleDto role = roleDtoMapper.apply(organizationUser.getRole());
        OrganizationDto organization = organizationDtoMapper.apply(organizationUser.getOrganization());
        result.setOrganization(organization);
        result.setRole(role);
        result.setId(organizationUser.getId());
        result.setActive(organizationUser.isActive());
        return result;

    }
}
