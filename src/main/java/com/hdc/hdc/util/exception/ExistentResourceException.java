package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.model.ApplicationException;

public class ExistentResourceException extends ApplicationException {
    public ExistentResourceException(String field) {
        super(field, "ja existe um " + field + "ja cadastrado no sistema para o usuário");
    }
}