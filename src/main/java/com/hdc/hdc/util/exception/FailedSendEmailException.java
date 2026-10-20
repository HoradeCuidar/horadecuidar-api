package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.model.ApplicationException;

public class FailedSendEmailException extends ApplicationException {
    public FailedSendEmailException(String field, String message) {
        super(field, message);
    }
    public FailedSendEmailException(String field) {
        super(field, "Falha no envio de e-mail via Resend API.");
    }
}
