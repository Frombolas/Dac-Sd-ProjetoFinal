package com.projetofinal.paymentservice.gateway;

import com.projetofinal.paymentservice.domain.PaymentStatus;
import com.projetofinal.paymentservice.exception.PaymentGatewayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class MercadoPagoGatewayClient implements PaymentGatewayClient {

    private final RestClient restClient;

    public MercadoPagoGatewayClient(@Value("${gateway.mercadopago.base-url}") String baseUrl,
                                     @Value("${gateway.mercadopago.access-token}") String accessToken) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    @Override
    public GatewayChargeResult criarCobranca(String descricao, BigDecimal valor) {
        try {
            Map<String, Object> response = restClient.post()
                    .uri("/v1/payments")
                    .header("X-Idempotency-Key", UUID.randomUUID().toString())
                    .body(Map.of(
                            "transaction_amount", valor,
                            "description", descricao,
                            "payment_method_id", "pix",
                            "payer", Map.of("email", "comprador@teste.com")
                    ))
                    .retrieve()
                    .body(Map.class);

            String id = String.valueOf(response.get("id"));
            String status = String.valueOf(response.get("status"));
            return new GatewayChargeResult(id, mapStatus(status));
        } catch (Exception ex) {
            throw new PaymentGatewayException("Falha ao comunicar com o gateway de pagamento", ex);
        }
    }

    private PaymentStatus mapStatus(String gatewayStatus) {
        return switch (gatewayStatus) {
            case "approved" -> PaymentStatus.APROVADO;
            case "rejected" -> PaymentStatus.RECUSADO;
            case "cancelled" -> PaymentStatus.CANCELADO;
            default -> PaymentStatus.PENDENTE;
        };
    }
}
