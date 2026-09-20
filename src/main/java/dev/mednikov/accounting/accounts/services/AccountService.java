package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.dto.AccountDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto updateAccount(AccountDto accountDto);

    void deleteAccount(UUID id);

    List<AccountDto> getAccounts(UUID organizationId, boolean includeDeprecated);

    Optional<AccountDto> getAccount(UUID id);

}
