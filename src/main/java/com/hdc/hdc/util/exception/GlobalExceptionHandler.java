package com.hdc.hdc.util.exception;

import com.hdc.hdc.util.exception.dto.ErrorResponseDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Tratamento das exceções personalizadas
    @ExceptionHandler(DeniedAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleDeniedAccessException(DeniedAccessException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getField(),
                ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ExistentResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleExistentResourceException(ExistentResourceException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getField(),
                ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ResourceWithSameNameException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceWithSameNameException(ResourceWithSameNameException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getField(),
                ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(error);
    }

    // Tratamento das demais exceções
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponseDTO>> handleValidationException(MethodArgumentNotValidException e) {
        List<ErrorResponseDTO> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponseDTO(
                        error.getField(),
                        error.getDefaultMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase()
                ))
                .toList();

        return ResponseEntity.badRequest().body(errors);
    }

    // Tratamento para BadRequestException (400)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(BadRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Campo inválido fornecido",
                ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Campo inválido fornecido",
                ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentialsException(BadCredentialsException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponseDTO error = new ErrorResponseDTO(
                "login",
                "Credenciais inválidas: verifique seu usuário e senha.",
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(error);
    }

    // Tratamento para Exceções Genéricas (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAllExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Campo inválido fornecido",
                ex.getMessage(),
                status.value(),
                status.getReasonPhrase()
        );
        return ResponseEntity.status(status).body(error);
    }
}
