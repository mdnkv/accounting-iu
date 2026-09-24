package dev.mednikov.accounting.currencies.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.mednikov.accounting.currencies.domain.CreateCurrencyRequestDto;
import dev.mednikov.accounting.currencies.domain.CurrencyResponseDto;
import dev.mednikov.accounting.currencies.domain.UpdateCurrencyRequestDto;

public interface CurrencyService {
    
    CurrencyResponseDto createCurrency (CreateCurrencyRequestDto requestDto);

    CurrencyResponseDto updateCurrency (UpdateCurrencyRequestDto requestDto);

    Optional<CurrencyResponseDto> getCurrencyById (UUID id);

    Optional<CurrencyResponseDto> getPrimaryCurrency(UUID organizationId);

    List<CurrencyResponseDto> getAllCurrencies(UUID organizationId);

}
