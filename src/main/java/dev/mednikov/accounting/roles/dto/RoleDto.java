package dev.mednikov.accounting.roles.dto;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

public final class RoleDto {

    private UUID id;
    @NotNull @NotBlank private UUID organizationId;
    @NotNull @NotBlank @Length(max=255) private String name;
    @NotNull @NotEmpty private List<AuthorityDto> authorities;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AuthorityDto> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<AuthorityDto> authorities) {
        this.authorities = authorities;
    }
}
