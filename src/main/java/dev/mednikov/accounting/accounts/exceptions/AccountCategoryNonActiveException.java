package dev.mednikov.accounting.accounts.exceptions;

import java.util.UUID;

public final class AccountCategoryNonActiveException extends RuntimeException{

    public AccountCategoryNonActiveException(UUID id) {
        super("Account category with id " + id.toString() + " is not active and cannot be used.");
    }
}
