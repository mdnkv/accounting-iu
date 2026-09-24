package dev.mednikov.accounting.currencies.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dev.mednikov.accounting.currencies.domain.CreateCurrencyRequestDto;
import dev.mednikov.accounting.currencies.domain.CurrencyResponseDto;
import dev.mednikov.accounting.currencies.domain.UpdateCurrencyRequestDto;
import dev.mednikov.accounting.currencies.services.CurrencyService;

@RestController
@RequestMapping("/currencies")
public class CurrencyRestController {
    
    private final CurrencyService currencyService;

    public CurrencyRestController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('currencies:create') and hasAuthority(#body.organizationId)")
    public @ResponseBody CurrencyResponseDto createCurrency (@RequestBody CreateCurrencyRequestDto body){
        return this.currencyService.createCurrency(body);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('currencies:update') and hasAuthority(#body.organizationId)")
    public @ResponseBody CurrencyResponseDto updateCurrency (@RequestBody UpdateCurrencyRequestDto body){
        return this.currencyService.updateCurrency(body);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAuthority('currencies:view')")
    public ResponseEntity<CurrencyResponseDto> getCurrencyById (@PathVariable UUID id){
        Optional<CurrencyResponseDto> result = this.currencyService.getCurrencyById(id);
        return ResponseEntity.of(result);
    }

    @GetMapping("/primary/{organizationId}")
    @PreAuthorize("hasAuthority('currencies:view')")
    public ResponseEntity<CurrencyResponseDto> getPrimaryCurrency (@PathVariable UUID organizationId){
        Optional<CurrencyResponseDto> result = this.currencyService.getPrimaryCurrency(organizationId);
        return ResponseEntity.of(result);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('currencies:view') and hasAuthority(#organizationId)")
    public @ResponseBody List<CurrencyResponseDto> getCurrencies(@PathVariable UUID organizationId)
    {
        return this.currencyService.getAllCurrencies(organizationId);
    }

}
