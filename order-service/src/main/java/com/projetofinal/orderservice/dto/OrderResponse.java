package com.projetofinal.orderservice.dto;

import com.projetofinal.orderservice.domain.Order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        String status,
        Instant criadoEm
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getDescricao(),
                order.getValor(),
                order.getStatus().name(),
                order.getCriadoEm()
        );
    }
}
