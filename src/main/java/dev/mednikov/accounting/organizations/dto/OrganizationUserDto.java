package dev.mednikov.accounting.organizations.dto;

import dev.mednikov.accounting.roles.dto.RoleDto;
import dev.mednikov.accounting.users.dto.UserDto;

import java.util.UUID;

public final class OrganizationUserDto {

    private UUID id;
    private UUID organizationId;
    private UUID roleId;
    private RoleDto role;
    private UserDto user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
