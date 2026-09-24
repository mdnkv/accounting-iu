package dev.mednikov.accounting.accounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException (UUID id){
        super("Account with id " + id.toString() + " does not exist");
    }
}
