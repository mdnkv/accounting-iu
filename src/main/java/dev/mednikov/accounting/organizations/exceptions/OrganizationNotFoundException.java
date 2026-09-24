package dev.mednikov.accounting.organizations.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(){}

    public OrganizationNotFoundException(UUID id) {
        super("Organization with id " + id.toString() + " does not exist");
    }

}
