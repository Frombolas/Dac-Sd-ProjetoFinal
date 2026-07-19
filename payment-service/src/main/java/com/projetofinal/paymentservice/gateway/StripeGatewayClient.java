package com.projetofinal.paymentservice.gateway;

import com.projetofinal.paymentservice.domain.PaymentStatus;
import com.projetofinal.paymentservice.exception.PaymentGatewayException;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StripeGatewayClient implements PaymentGatewayClient {

    // Payment method de teste do Stripe: cartao Visa que aprova automaticamente,
    // usavel direto via API sem precisar de Stripe.js no frontend.
    private static final String TEST_PAYMENT_METHOD = "pm_card_visa";

    private final RequestOptions requestOptions;

    public StripeGatewayClient(@Value("${gateway.stripe.secret-key}") String secretKey) {
        this.requestOptions = RequestOptions.builder().setApiKey(secretKey).build();
    }

    @Override
    public GatewayChargeResult criarCobranca(String descricao, BigDecimal valor) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(valor.movePointRight(2).longValueExact())
                    .setCurrency("brl")
                    .setDescription(descricao)
                    .setPaymentMethod(TEST_PAYMENT_METHOD)
                    .addPaymentMethodType("card")
                    .setConfirm(true)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params, requestOptions);
            return new GatewayChargeResult(intent.getId(), mapStatus(intent.getStatus()));
        } catch (CardException ex) {
            return new GatewayChargeResult(ex.getRequestId(), PaymentStatus.RECUSADO);
        } catch (StripeException ex) {
            throw new PaymentGatewayException("Falha ao comunicar com o gateway de pagamento", ex);
        }
    }

    private PaymentStatus mapStatus(String status) {
        return switch (status) {
            case "succeeded" -> PaymentStatus.APROVADO;
            case "canceled" -> PaymentStatus.CANCELADO;
            default -> PaymentStatus.PENDENTE;
        };
    }
}
