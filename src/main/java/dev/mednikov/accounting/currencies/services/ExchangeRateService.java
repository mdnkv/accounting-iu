package dev.mednikov.accounting.currencies.services;

import java.time.LocalDate;

import dev.mednikov.accounting.currencies.domain.ExchangeRateDto;
import dev.mednikov.accounting.currencies.models.Currency;

public interface ExchangeRateService {

    ExchangeRateDto getExchangeRate (Currency basCurrency, Currency targetCurrency, LocalDate date); 

}
