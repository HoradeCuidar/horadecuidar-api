package com.hdc.hdc.util.exception.model;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {
    private final String field;

    protected ApplicationException(String field, String message) {
        super(message);
        this.field = field;
    }
}
