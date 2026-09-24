package dev.mednikov.accounting.accounts.domain;

import dev.mednikov.accounting.accounts.models.AccountType;

import java.util.UUID;

public record UpdateAccountCategoryRequestDto(UUID id, String name, AccountType accountType, UUID organizationId, boolean active) {
}
