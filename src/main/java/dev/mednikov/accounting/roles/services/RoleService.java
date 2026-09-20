package dev.mednikov.accounting.roles.services;

import dev.mednikov.accounting.roles.dto.RoleDto;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleDto createRole (RoleDto payload);

    RoleDto updateRole (RoleDto payload);

    void deleteRole (UUID id);

    List<RoleDto> getRoles (UUID organizationId);

    void addAuthorityToRole (UUID roleId, UUID authorityId);

    void removeAuthorityFromRole (UUID roleId, UUID authorityId);

}
