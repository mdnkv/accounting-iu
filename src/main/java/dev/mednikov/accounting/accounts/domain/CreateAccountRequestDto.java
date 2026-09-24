package dev.mednikov.accounting.accounts.domain;

import dev.mednikov.accounting.accounts.models.AccountBalanceType;

import java.util.UUID;

public record CreateAccountRequestDto (UUID categoryId, String code, String name, AccountBalanceType normalBalance, UUID organizationId) {
}
