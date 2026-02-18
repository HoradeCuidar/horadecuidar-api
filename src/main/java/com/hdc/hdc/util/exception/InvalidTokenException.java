package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.model.ApplicationException;

public class InvalidTokenException extends ApplicationException {
    public InvalidTokenException(String field, String message) {
        super(field, message);
    }

    public InvalidTokenException() {
        super("token", "O token informado é inválido.");
    }
}
