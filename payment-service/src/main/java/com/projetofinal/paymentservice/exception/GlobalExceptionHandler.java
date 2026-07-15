package com.projetofinal.paymentservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PaymentNotFoundException ex) {
        // TODO (Pessoa 2): retornar 404 (HttpStatus.NOT_FOUND) com uma mensagem de erro no corpo
        throw new UnsupportedOperationException("TODO: implementar handleNotFound");
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<Map<String, Object>> handleGateway(PaymentGatewayException ex) {
        // TODO (Pessoa 2): retornar 502 (Bad Gateway) com a mensagem de erro do gateway
        throw new UnsupportedOperationException("TODO: implementar handleGateway");
    }
}
