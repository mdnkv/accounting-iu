package dev.mednikov.accounting.roles.controllers;

import dev.mednikov.accounting.roles.dto.RoleDto;
import dev.mednikov.accounting.roles.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleRestController {

    private final RoleService roleService;

    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('roles:create') and hasAuthority(#body.organizationId)")
    public @ResponseBody RoleDto createRole (@RequestBody @Valid RoleDto body){
        return this.roleService.createRole(body);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('roles:update') and hasAuthority(#body.organizationId)")
    public @ResponseBody RoleDto updateRole (@RequestBody @Valid RoleDto body){
        return this.roleService.updateRole(body);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('roles:delete')")
    public void deleteRole (@PathVariable UUID id){
        this.roleService.deleteRole(id);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('roles:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<RoleDto> getRoles (@PathVariable UUID organizationId){
        return this.roleService.getRoles(organizationId);
    }

    @PostMapping("/authority/add/{roleId}/{authorityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('roles:update')")
    public void addAuthorityToRole (@PathVariable UUID roleId, @PathVariable UUID authorityId){
        this.roleService.addAuthorityToRole(roleId, authorityId);
    }

    @PostMapping("/authority/remove/{roleId}/{authorityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('roles:update')")
    public void removeAuthorityFromRole (@PathVariable UUID roleId, @PathVariable UUID authorityId){
        this.roleService.removeAuthorityFromRole(roleId, authorityId);
    }

}
