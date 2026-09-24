package dev.mednikov.accounting.currencies.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import dev.mednikov.accounting.currencies.domain.ExchangeRateDto;
import dev.mednikov.accounting.currencies.models.Currency;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Override
    public ExchangeRateDto getExchangeRate(Currency basCurrency, Currency targetCurrency, LocalDate date) {
        return new ExchangeRateDto(BigDecimal.ONE);
    }

    
    
}
