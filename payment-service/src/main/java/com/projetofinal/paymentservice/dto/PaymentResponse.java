package com.projetofinal.paymentservice.dto;

import com.projetofinal.paymentservice.domain.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long pedidoId,
        BigDecimal valor,
        String status,
        String gatewayTransactionId,
        Instant criadoEm
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPedidoId(),
                payment.getValor(),
                payment.getStatus().name(),
                payment.getGatewayTransactionId(),
                payment.getCriadoEm()
        );
    }
}
