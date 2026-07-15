package com.projetofinal.paymentservice.gateway;

import java.math.BigDecimal;

public interface PaymentGatewayClient {

    GatewayChargeResult criarCobranca(String descricao, BigDecimal valor);
}
