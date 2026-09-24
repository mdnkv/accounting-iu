package dev.mednikov.accounting.accounts.services;

import dev.mednikov.accounting.accounts.domain.AccountCategoryResponseDto;
import dev.mednikov.accounting.accounts.domain.CreateAccountCategoryRequestDto;
import dev.mednikov.accounting.accounts.domain.UpdateAccountCategoryRequestDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountCategoryService {

    AccountCategoryResponseDto createAccountCategory (CreateAccountCategoryRequestDto requestDto);

    AccountCategoryResponseDto updateAccountCategory (UpdateAccountCategoryRequestDto requestDto);

    Optional<AccountCategoryResponseDto> getAccountCategoryById (UUID id);

    List<AccountCategoryResponseDto> getAllAccountCategories(UUID organizationId);


}
