package dev.mednikov.accounting.accounts.controllers;

import dev.mednikov.accounting.accounts.dto.AccountCategoryDto;
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
    public @ResponseBody AccountCategoryDto createAccountCategory(@RequestBody @Valid AccountCategoryDto body) {
        return this.accountCategoryService.createAccountCategory(body);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('accounts:update') and hasAuthority(#body.organizationId)")
    public @ResponseBody AccountCategoryDto updateAccountCategory(@RequestBody @Valid AccountCategoryDto body) {
        return this.accountCategoryService.updateAccountCategory(body);
    }

    @DeleteMapping("/delete/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('accounts:delete')")
    public void deleteAccountCategory(@PathVariable UUID accountId) {
        this.accountCategoryService.deleteAccountCategory(accountId);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('accounts:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<AccountCategoryDto> getAccounts(@PathVariable UUID organizationId ) {
        return this.accountCategoryService.getAccountCategories(organizationId);
    }

}
