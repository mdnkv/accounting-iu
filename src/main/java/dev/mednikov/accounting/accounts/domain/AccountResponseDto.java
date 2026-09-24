package dev.mednikov.accounting.accounts.domain;

import dev.mednikov.accounting.accounts.models.AccountBalanceType;

import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        AccountCategoryResponseDto accountCategory,
        String code,
        String name,
        AccountBalanceType normalBalance,
        UUID organizationId,
        boolean active) {
}
