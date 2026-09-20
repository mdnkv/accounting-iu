package dev.mednikov.accounting.accounts.controllers;

import dev.mednikov.accounting.accounts.dto.AccountDto;
import dev.mednikov.accounting.accounts.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('accounts:create') and hasAuthority(#body.organizationId)")
    public @ResponseBody AccountDto createAccount(@RequestBody @Valid AccountDto body) {
        return this.accountService.createAccount(body);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('accounts:update') and hasAuthority(#body.organizationId)")
    public @ResponseBody AccountDto updateAccount(@RequestBody @Valid AccountDto body) {
        return this.accountService.updateAccount(body);
    }

    @DeleteMapping("/delete/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('accounts:delete')")
    public void deleteAccount(@PathVariable UUID accountId) {
        this.accountService.deleteAccount(accountId);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('accounts:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<AccountDto> getAccounts(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "true", required = false) boolean includeDeprecated
    ) {
        return this.accountService.getAccounts(organizationId, includeDeprecated);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAuthority('accounts:view')")
    public ResponseEntity<AccountDto> getAccount(@PathVariable UUID accountId) {
        Optional<AccountDto> accountDto = this.accountService.getAccount(accountId);
        return ResponseEntity.of(accountDto);
    }

}
