package dev.mednikov.accounting.accounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class AccountAlreadyExistsException extends RuntimeException{

    public AccountAlreadyExistsException(String code, UUID organizationId) {
        super("Account with code " + code + " already exists in organization " + organizationId.toString());
    }


}
