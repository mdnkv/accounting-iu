package dev.mednikov.accounting.accounts.domain;

import dev.mednikov.accounting.accounts.models.AccountType;

import java.util.UUID;

public record CreateAccountCategoryRequestDto(String name, AccountType accountType, UUID organizationId) {
}
