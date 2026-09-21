package com.sportflow.core.errors;

public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }

    public BusinessRuleException(String code, String message) {
        super(code, message);
    }
}
