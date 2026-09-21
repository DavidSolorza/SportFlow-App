package com.sportflow.core.errors;

public class ConflictException extends DomainException {
    public ConflictException(String code, String message) {
        super(code, message);
    }
}
