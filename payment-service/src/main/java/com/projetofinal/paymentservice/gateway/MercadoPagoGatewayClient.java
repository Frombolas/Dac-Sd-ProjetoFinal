package com.projetofinal.paymentservice.gateway;

import com.projetofinal.paymentservice.domain.PaymentStatus;
import com.projetofinal.paymentservice.exception.PaymentGatewayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

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

            Map<String, Object> paymentPayload = Map.of(
                    "valor_transacao", valor,
                    "token", cardToken,
                    "descricao", descricao,
                    "parcelas", 1,
                    "id_metado_pagamento", "master",
                    "pagadora", Map.of(
                            "email", payerEmail,
                            "identificacao", Map.of(
                                    "tipo", "CPF",
                                    "numero_cpf", CPF_TESTE
                            )
                    )
            );

            Map<?, ?> response = restClient.post()
                    .uri("/v1/payments")
                    .body(paymentPayload)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new PaymentGatewayException("Resposta nula recebida do Mercado Pago", null);
            }

            String transactionId = String.valueOf(response.get("id"));
            String gatewayStatus = String.valueOf(response.get("status"));
            PaymentStatus status = mapStatus(gatewayStatus);

            return new GatewayChargeResult(transactionId, status);

        } catch (Exception e) {
            throw new PaymentGatewayException("Falha ao processar cobrança no Mercado Pago: " + e.getMessage(), e);
        }
    }

    private String tokenizarCartaoDeTeste() {
        try {
            Map<String, Object> cardPayload = Map.of(
                    "numero_cartao", CARTAO_NUMERO,
                    "cvv_cartao", CARTAO_CVV,
                    "mes_expiracao", Integer.parseInt(CARTAO_MES),
                    "ano_expiracao", Integer.parseInt(CARTAO_ANO),
                    "titular_cartao", Map.of(
                            "nome_titular", CARTAO_TITULAR,
                            "identification", Map.of(
                                    "tipo", "CPF",
                                    "numero_cpf", CPF_TESTE
                            )
                    )
            );

            Map<?, ?> response = restClient.post()
                    .uri("/v1/card_tokens?public_key=" + publicKey)
                    .body(cardPayload)
                    .retrieve()
                    .body(Map.class);

            if (response == null || !response.containsKey("id")) {
                throw new PaymentGatewayException("Falha ao gerar token de teste do cartão no Mercado Pago", null);
            }

            return String.valueOf(response.get("id"));

        } catch (Exception e) {
            throw new PaymentGatewayException("Erro na tokenização do cartão: " + e.getMessage(), e);
        }
    }

    private PaymentStatus mapStatus(String gatewayStatus) {
        if (gatewayStatus == null) return PaymentStatus.PENDENTE;

        return switch (gatewayStatus.toLowerCase()) {
            case "aprovado" -> PaymentStatus.APROVADO;
            case "recusado" -> PaymentStatus.RECUSADO;
            case "cancelado" -> PaymentStatus.CANCELADO;
            default -> PaymentStatus.PENDENTE;
        };
    }
}