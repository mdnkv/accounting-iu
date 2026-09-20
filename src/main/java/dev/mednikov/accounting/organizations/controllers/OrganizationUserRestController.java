package dev.mednikov.accounting.organizations.controllers;

import dev.mednikov.accounting.organizations.dto.CreateOrganizationUserRequestDto;
import dev.mednikov.accounting.organizations.dto.OrganizationUserDto;
import dev.mednikov.accounting.organizations.dto.UserOrganizationDto;
import dev.mednikov.accounting.organizations.services.OrganizationUserService;
import dev.mednikov.accounting.users.models.User;
import dev.mednikov.accounting.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/organization-users")
public class OrganizationUserRestController {

    private final OrganizationUserService organizationUserService;
    private final UserService userService;

    public OrganizationUserRestController(OrganizationUserService organizationUserService, UserService userService) {
        this.organizationUserService = organizationUserService;
        this.userService = userService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority(#body.organizationId) and hasAuthority('organization-users:create')")
    public ResponseEntity<OrganizationUserDto> createOrganizationUser (@RequestBody @Valid CreateOrganizationUserRequestDto body){
        Optional<OrganizationUserDto> result = this.organizationUserService.createOrganizationUser(body);
        return ResponseEntity.of(result);
//        if (result.isPresent()){
//            return ResponseEntity.status(HttpStatus.CREATED).body(result.get());
//        } else {
//            return ResponseEntity.noContent().build();
//        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority(#body.organizationId) and hasAuthority('organization-users:update')")
    public @ResponseBody OrganizationUserDto updateOrganizationUser (@RequestBody OrganizationUserDto body){
        return this.organizationUserService.updateOrganizationUser(body);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('organization-users:delete')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganizationUser (@PathVariable UUID id){
        this.organizationUserService.deleteOrganizationUser(id);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority(#organizationId) and hasAuthority('organization-users:view')")
    public @ResponseBody List<OrganizationUserDto> getUsersInOrganization (@PathVariable UUID organizationId){
        return this.organizationUserService.getUsersInOrganization(organizationId);
    }


    @GetMapping("/current/all")
    public @ResponseBody List<UserOrganizationDto> getAllForUser(@AuthenticationPrincipal Jwt jwt) {
        User user = this.userService.getOrCreateUser(jwt);
        return this.organizationUserService.getAllForUser(user);
    }

    @PostMapping("/current/active/{id}")
    public @ResponseBody UserOrganizationDto setActiveForUser (@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        User user = this.userService.getOrCreateUser(jwt);
        return this.organizationUserService.setActiveForUser(user, id);
    }

    @GetMapping("/current/active")
    public ResponseEntity<UserOrganizationDto> getActiveForUser (@AuthenticationPrincipal Jwt jwt) {
        User user = this.userService.getOrCreateUser(jwt);
        Optional<UserOrganizationDto> result = this.organizationUserService.getActiveForUser(user);
        return ResponseEntity.of(result);
    }

}
