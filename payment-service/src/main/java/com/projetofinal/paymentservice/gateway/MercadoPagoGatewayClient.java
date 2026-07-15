package com.projetofinal.paymentservice.gateway;

import com.projetofinal.paymentservice.domain.PaymentStatus;
import com.projetofinal.paymentservice.exception.PaymentGatewayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class MercadoPagoGatewayClient implements PaymentGatewayClient {

    // Cartao de teste Mercado Pago (Mastercard) - aprova automaticamente pelo nome "APRO" no titular.
    private static final String CARTAO_NUMERO = "5031433215406351";
    private static final String CARTAO_CVV = "123";
    private static final String CARTAO_MES = "11";
    private static final String CARTAO_ANO = "2030";
    private static final String CARTAO_TITULAR = "APRO";
    private static final String CPF_TESTE = "12345678909";

    private final RestClient restClient;
    private final String publicKey;
    private final String payerEmail;

    public MercadoPagoGatewayClient(@Value("${gateway.mercadopago.base-url}") String baseUrl,
                                     @Value("${gateway.mercadopago.access-token}") String accessToken,
                                     @Value("${gateway.mercadopago.public-key}") String publicKey,
                                     @Value("${gateway.mercadopago.payer-email}") String payerEmail) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
        this.publicKey = publicKey;
        this.payerEmail = payerEmail;
    }

    @Override
    public GatewayChargeResult criarCobranca(String descricao, BigDecimal valor) {
        try {
            String cardToken = tokenizarCartaoDeTeste();

            Map<String, Object> response = restClient.post()
                    .uri("/v1/payments")
                    .header("X-Idempotency-Key", UUID.randomUUID().toString())
                    .body(Map.of(
                            "transaction_amount", valor,
                            "token", cardToken,
                            "description", descricao,
                            "installments", 1,
                            "payment_method_id", "master",
                            "payer", Map.of(
                                    "email", payerEmail,
                                    "identification", Map.of("type", "CPF", "number", CPF_TESTE)
                            )
                    ))
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            String id = String.valueOf(response.get("id"));
            String status = String.valueOf(response.get("status"));
            return new GatewayChargeResult(id, mapStatus(status));
        } catch (Exception ex) {
            throw new PaymentGatewayException("Falha ao comunicar com o gateway de pagamento", ex);
        }
    }

    private String tokenizarCartaoDeTeste() {
        Map<String, Object> response = restClient.post()
                .uri("/v1/card_tokens?public_key={publicKey}", publicKey)
                .body(Map.of(
                        "card_number", CARTAO_NUMERO,
                        "expiration_month", CARTAO_MES,
                        "expiration_year", CARTAO_ANO,
                        "security_code", CARTAO_CVV,
                        "cardholder", Map.of(
                                "name", CARTAO_TITULAR,
                                "identification", Map.of("type", "CPF", "number", CPF_TESTE)
                        )
                ))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        return String.valueOf(response.get("id"));
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
