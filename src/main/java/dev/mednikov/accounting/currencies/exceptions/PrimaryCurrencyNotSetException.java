package dev.mednikov.accounting.currencies.exceptions;

import java.util.UUID;

public final class PrimaryCurrencyNotSetException extends RuntimeException {

    public PrimaryCurrencyNotSetException(UUID organizationId){
        super("Primary currency is not set for the organization " + organizationId.toString());
    }
    
}
