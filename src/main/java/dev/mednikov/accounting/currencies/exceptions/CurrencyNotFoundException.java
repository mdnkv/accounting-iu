package dev.mednikov.accounting.currencies.exceptions;

import java.util.UUID;

public final class CurrencyNotFoundException extends RuntimeException {
    
    public CurrencyNotFoundException(UUID id){
        super("Currency with id " + id.toString() + " does not exist");
    }

}
