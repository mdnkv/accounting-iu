package dev.mednikov.accounting.accounts.dto;

import dev.mednikov.accounting.accounts.models.AccountCategory;

import java.util.function.Function;

public final class AccountCategoryDtoMapper implements Function<AccountCategory, AccountCategoryDto> {

    @Override
    public AccountCategoryDto apply(AccountCategory accountCategory) {
        AccountCategoryDto result = new AccountCategoryDto();
        result.setId(accountCategory.getId());
        result.setName(accountCategory.getName());
        result.setAccountType(accountCategory.getAccountType());
        result.setOrganizationId(accountCategory.getOrganization().getId());
        return result;
    }

}
