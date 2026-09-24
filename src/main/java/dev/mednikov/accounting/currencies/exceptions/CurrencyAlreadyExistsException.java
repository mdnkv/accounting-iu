package dev.mednikov.accounting.currencies.exceptions;

import java.util.UUID;

public final class CurrencyAlreadyExistsException extends RuntimeException {

    public CurrencyAlreadyExistsException(String code, UUID organizationId){
        super("Currency with code " + code + " already exists in organization " + organizationId.toString());
    }

    
}
