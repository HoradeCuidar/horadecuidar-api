package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.model.ApplicationException;

public class EntityInUseException extends ApplicationException {

    public EntityInUseException(String field) {
        super(field, "Não é possível excluir esta prescrição, pois existem registros de adesão vinculados a ela.");
    }

    public  EntityInUseException(String field, String message) {
        super(field, message);
    }
}
