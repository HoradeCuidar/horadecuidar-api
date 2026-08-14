package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.model.ApplicationException;

public class ResourceNotFoundException extends ApplicationException {
  public ResourceNotFoundException(String field) {
    super(field, "recurso nao encontrado");
  }
  public ResourceNotFoundException(String field, String message) {
    super(field, message);
  }

}
