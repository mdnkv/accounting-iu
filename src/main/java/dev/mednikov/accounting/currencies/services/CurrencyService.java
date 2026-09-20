package dev.mednikov.accounting.currencies.services;

import dev.mednikov.accounting.currencies.dto.CurrencyDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurrencyService {

    CurrencyDto createCurrency(CurrencyDto currencyDto);

    CurrencyDto updateCurrency(CurrencyDto currencyDto);

    Optional<CurrencyDto> getPrimaryCurrency (UUID organizationId);

    List<CurrencyDto> getCurrencies(UUID organizationId);

    void deleteCurrency (UUID id);

}
