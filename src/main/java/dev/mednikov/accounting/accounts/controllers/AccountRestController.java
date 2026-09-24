package dev.mednikov.accounting.accounts.controllers;

import dev.mednikov.accounting.accounts.domain.AccountResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountRequestDto;
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
    public @ResponseBody AccountResponseDto createAccount(@RequestBody @Valid CreateAccountRequestDto body) {
        return this.accountService.createAccount(body);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('accounts:update') and hasAuthority(#body.organizationId)")
    public @ResponseBody AccountResponseDto updateAccount(@RequestBody @Valid UpdateAccountRequestDto body) {
        return this.accountService.updateAccount(body);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('accounts:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<AccountResponseDto> getAccounts(@PathVariable UUID organizationId) {
        return this.accountService.getAllAccounts(organizationId);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAuthority('accounts:view')")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable UUID accountId) {
        Optional<AccountResponseDto> accountDto = this.accountService.getAccountById(accountId);
        return ResponseEntity.of(accountDto);
    }

}
