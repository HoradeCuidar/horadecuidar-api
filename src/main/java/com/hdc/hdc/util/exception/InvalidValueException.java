package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.model.ApplicationException;

public class InvalidValueException extends ApplicationException {
    public InvalidValueException(String field) {
        super(field, "O valor informado em " + field + " é inválido.");
    }

    public InvalidValueException(String field, String message) {
        super(field, message);
    }
}
