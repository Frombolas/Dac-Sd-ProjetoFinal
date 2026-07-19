package com.projetofinal.paymentservice.service;

import com.projetofinal.paymentservice.domain.Payment;
import com.projetofinal.paymentservice.domain.PaymentStatus;
import com.projetofinal.paymentservice.event.OrderCreatedEvent;
import com.projetofinal.paymentservice.exception.PaymentNotFoundException;
import com.projetofinal.paymentservice.gateway.GatewayChargeResult;
import com.projetofinal.paymentservice.gateway.PaymentGatewayClient;
import com.projetofinal.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGatewayClient gatewayClient;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, gatewayClient);
    }

    @Test
    void deveProcessarPagamentoAprovado() {
        var event = new OrderCreatedEvent(1L, "Assinatura mensal", new BigDecimal("99.90"), Instant.now());
        when(gatewayClient.criarCobranca(event.descricao(), event.valor()))
                .thenReturn(new GatewayChargeResult("mp-123", PaymentStatus.APROVADO));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.process(event);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.APROVADO);
        assertThat(payment.getGatewayTransactionId()).isEqualTo("mp-123");
        assertThat(payment.getPedidoId()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoNaoExiste() {
        when(paymentRepository.findByPedidoId(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.findByPedidoId(1L));
    }

    @Test
    void deveListarTodosOsPagamentos() {
        when(paymentRepository.findAll()).thenReturn(
                List.of(new Payment(1L, new BigDecimal("10.00"), PaymentStatus.PENDENTE, "id")));

        List<Payment> pagamentos = paymentService.findAll();

        assertThat(pagamentos).hasSize(1);
    }
}
