package com.projetofinal.paymentservice.gateway;

import com.projetofinal.paymentservice.domain.PaymentStatus;

public record GatewayChargeResult(String transactionId, PaymentStatus status) {
}
