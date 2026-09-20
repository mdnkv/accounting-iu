package dev.mednikov.accounting.organizations.dto;

import dev.mednikov.accounting.organizations.models.OrganizationUser;
import dev.mednikov.accounting.roles.dto.RoleDto;
import dev.mednikov.accounting.roles.dto.RoleDtoMapper;
import dev.mednikov.accounting.users.dto.UserDto;
import dev.mednikov.accounting.users.dto.UserDtoMapper;

import java.util.function.Function;

public final class OrganizationUserDtoMapper implements Function<OrganizationUser, OrganizationUserDto> {

    private final static RoleDtoMapper roleDtoMapper = new RoleDtoMapper();
    private final static UserDtoMapper userDtoMapper = new UserDtoMapper();

    @Override
    public OrganizationUserDto apply(OrganizationUser organizationUser) {
        RoleDto role = roleDtoMapper.apply(organizationUser.getRole());
        OrganizationUserDto result = new OrganizationUserDto();
        UserDto user = userDtoMapper.apply(organizationUser.getUser());
        result.setRole(role);
        result.setId(organizationUser.getId());
        result.setRoleId(role.getId());
        result.setOrganizationId(organizationUser.getOrganization().getId());
        result.setUser(user);
        return result;
    }

}
