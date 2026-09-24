package dev.mednikov.accounting.currencies.exceptions;

import java.util.UUID;

public final class CurrencyNonActiveException extends RuntimeException {

    public CurrencyNonActiveException(UUID id) {
        super("Currency with id " + id.toString() + " is not active and cannot be used.");
    }
}
