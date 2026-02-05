package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.model.ApplicationException;

public class DeniedAccessException extends ApplicationException {
    public DeniedAccessException(String field, String message) {
        super(field, message);
    }
    public DeniedAccessException(String field) {
        super(field, "Acesso negado");
    }
}
