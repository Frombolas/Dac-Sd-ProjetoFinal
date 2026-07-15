package com.projetofinal.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Pedido nao encontrado: " + id);
    }
}
