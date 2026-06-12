package com.hdc.hdc.util.exception.model;

public class InvalidOperationException extends ApplicationException{
    public InvalidOperationException(String field) {
        super(field, "A operação não pode ser realizada!");
    }

    public InvalidOperationException(String field, String message) {
        super(field, message);
    }
}
