package dev.mednikov.accounting.accounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public final class AccountCategoryAlreadyExistsException extends RuntimeException {

    public AccountCategoryAlreadyExistsException(String name, UUID organizationId) {
        super("Account category with name " + name + " already exists in organization " + organizationId.toString());
    }
}
