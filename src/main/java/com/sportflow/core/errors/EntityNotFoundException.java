package com.sportflow.core.errors;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String message) {
        super("ENTITY_NOT_FOUND", message);
    }

    public EntityNotFoundException(String code, String message) {
        super(code, message);
    }
}
