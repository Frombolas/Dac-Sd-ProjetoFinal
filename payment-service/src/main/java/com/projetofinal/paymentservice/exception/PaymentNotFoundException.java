package com.projetofinal.paymentservice.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long pedidoId) {
        super("Pagamento nao encontrado para o pedido: " + pedidoId);
    }
}
