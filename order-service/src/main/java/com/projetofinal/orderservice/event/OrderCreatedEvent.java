package com.projetofinal.orderservice.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        Long pedidoId,
        String descricao,
        BigDecimal valor,
        Instant criadoEm
) {
}
