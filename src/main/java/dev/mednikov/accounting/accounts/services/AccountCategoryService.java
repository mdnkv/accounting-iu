package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.dto.AccountCategoryDto;

import java.util.List;
import java.util.UUID;

public interface AccountCategoryService {

    AccountCategoryDto createAccountCategory(AccountCategoryDto accountCategoryDto);

    AccountCategoryDto updateAccountCategory(AccountCategoryDto accountCategoryDto);

    void deleteAccountCategory(UUID id);

    List<AccountCategoryDto> getAccountCategories(UUID organizationId);

}
