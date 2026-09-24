package dev.mednikov.accounting.currencies.domain;

import java.util.UUID;

public record CreateCurrencyRequestDto(String code, String name, UUID organizationId) {
    
}
