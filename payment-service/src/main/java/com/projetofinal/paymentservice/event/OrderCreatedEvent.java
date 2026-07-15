package com.projetofinal.paymentservice.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        Long pedidoId,
        String descricao,
        BigDecimal valor,
        Instant criadoEm
) {
}
