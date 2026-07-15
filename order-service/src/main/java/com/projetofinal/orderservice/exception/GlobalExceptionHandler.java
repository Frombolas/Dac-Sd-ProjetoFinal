package com.projetofinal.orderservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(OrderNotFoundException ex) {
        // TODO (Pessoa 1): retornar 404 (HttpStatus.NOT_FOUND) com uma mensagem de erro no corpo
        throw new UnsupportedOperationException("TODO: implementar handleNotFound");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // TODO (Pessoa 1): retornar 400 (Bad Request) com as mensagens dos campos invalidos
        // (ex.getBindingResult().getFieldErrors())
        throw new UnsupportedOperationException("TODO: implementar handleValidation");
    }
}
