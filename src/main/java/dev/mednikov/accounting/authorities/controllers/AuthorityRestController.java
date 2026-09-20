package dev.mednikov.accounting.authorities.controllers;

import dev.mednikov.accounting.authorities.dto.AuthorityDto;
import dev.mednikov.accounting.authorities.services.AuthorityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/authorities")
public class AuthorityRestController {

    private final AuthorityService authorityService;

    public AuthorityRestController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('authorities:create') and hasAuthority(#body.organizationId)")
    public @ResponseBody AuthorityDto createAuthority(@RequestBody @Valid AuthorityDto body) {
        return this.authorityService.createAuthority(body);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('authorities:update') and hasAuthority(#body.organizationId)")
    public @ResponseBody AuthorityDto updateAuthority(@RequestBody @Valid AuthorityDto body) {
        return this.authorityService.updateAuthority(body);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('authorities:delete')")
    public void deleteAuthority (@PathVariable UUID id){
        this.authorityService.deleteAuthority(id);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('authorities:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<AuthorityDto> getAuthorities(@PathVariable UUID organizationId){
        return this.authorityService.getAuthorities(organizationId);
    }

}
