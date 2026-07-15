package com.projetofinal.paymentservice.gateway;

import com.projetofinal.paymentservice.domain.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

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
        // TODO (Pessoa 2):
        // 1) chamar tokenizarCartaoDeTeste() para obter o token do cartao
        // 2) fazer POST /v1/payments (transaction_amount, token, installments, payment_method_id "master",
        //    payer com "email" e "identification") usando o restClient, envolvendo erros em PaymentGatewayException
        // 3) mapear o campo "status" da resposta com mapStatus(...) e devolver um GatewayChargeResult
        throw new UnsupportedOperationException("TODO: implementar MercadoPagoGatewayClient.criarCobranca");
    }

    private String tokenizarCartaoDeTeste() {
        // TODO (Pessoa 2): POST /v1/card_tokens?public_key={publicKey} com os dados do cartao de teste
        // acima (CARTAO_NUMERO, CARTAO_CVV, CARTAO_MES, CARTAO_ANO, CARTAO_TITULAR, CPF_TESTE)
        // e retornar o "id" do token gerado na resposta
        throw new UnsupportedOperationException("TODO: implementar tokenizarCartaoDeTeste");
    }

    private PaymentStatus mapStatus(String gatewayStatus) {
        // TODO (Pessoa 2): mapear "approved" -> APROVADO, "rejected" -> RECUSADO,
        // "cancelled" -> CANCELADO, qualquer outro -> PENDENTE
        throw new UnsupportedOperationException("TODO: implementar mapStatus");
    }
}
