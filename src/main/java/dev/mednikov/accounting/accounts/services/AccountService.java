package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.domain.AccountResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    AccountResponseDto createAccount (CreateAccountRequestDto requestDto);

    AccountResponseDto updateAccount (UpdateAccountRequestDto requestDto);

    Optional<AccountResponseDto> getAccountById(UUID id);

    List<AccountResponseDto> getAllAccounts(UUID organizationId);

}
