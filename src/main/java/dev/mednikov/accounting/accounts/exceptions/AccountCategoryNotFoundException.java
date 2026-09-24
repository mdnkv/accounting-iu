package dev.mednikov.accounting.accounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class AccountCategoryNotFoundException extends RuntimeException {

    public AccountCategoryNotFoundException (UUID id){
        super("Account category with id " + id.toString() + " does not exist");
    }

}
