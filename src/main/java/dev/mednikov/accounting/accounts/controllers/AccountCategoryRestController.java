package dev.mednikov.accounting.accounts.controllers;

import dev.mednikov.accounting.accounts.domain.AccountCategoryResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountCategoryRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountCategoryRequestDto;
import dev.mednikov.accounting.accounts.services.AccountCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account-categories")
public class AccountCategoryRestController {

    private final AccountCategoryService accountCategoryService;

    public AccountCategoryRestController(AccountCategoryService accountCategoryService) {
        this.accountCategoryService = accountCategoryService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('accounts:create') and hasAuthority(#body.organizationId)")
    public @ResponseBody AccountCategoryResponseDto createAccountCategory(@RequestBody @Valid CreateAccountCategoryRequestDto body) {
        return this.accountCategoryService.createAccountCategory(body);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('accounts:update') and hasAuthority(#body.organizationId)")
    public @ResponseBody AccountCategoryResponseDto updateAccountCategory(@RequestBody @Valid UpdateAccountCategoryRequestDto body) {
        return this.accountCategoryService.updateAccountCategory(body);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('accounts:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<AccountCategoryResponseDto> getAccounts(@PathVariable UUID organizationId ) {
        return this.accountCategoryService.getAllAccountCategories(organizationId);
    }

}
