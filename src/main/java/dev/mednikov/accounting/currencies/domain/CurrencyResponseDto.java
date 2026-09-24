package dev.mednikov.accounting.currencies.domain;

import java.util.UUID;

public record CurrencyResponseDto(
        UUID id,
        String code,
        String name,
        UUID organizationId,
        boolean active,
        boolean primary) {
    
}
