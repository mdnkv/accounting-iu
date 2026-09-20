package dev.mednikov.accounting.accounts.dto;

import dev.mednikov.accounting.accounts.models.Account;
import dev.mednikov.accounting.accounts.models.AccountCategory;

import java.util.function.Function;

public final class AccountDtoMapper implements Function<Account, AccountDto> {

    private final static AccountCategoryDtoMapper accountCategoryDtoMapper = new AccountCategoryDtoMapper();

    @Override
    public AccountDto apply(Account account) {
        AccountDto result = new AccountDto();
        result.setId(account.getId());
        result.setName(account.getName());
        result.setCode(account.getCode());
        result.setAccountType(account.getAccountType());
        result.setOrganizationId(account.getOrganization().getId());
        result.setDeprecated(account.isDeprecated());
        if (account.getAccountCategory().isPresent()) {
            AccountCategory accountCategory = account.getAccountCategory().get();
            result.setAccountCategory(accountCategoryDtoMapper.apply(accountCategory));
            result.setAccountCategoryId(accountCategory.getId());
        } else {
            result.setAccountCategory(null);
            result.setAccountCategoryId(null);
        }
        return result;
    }

}
