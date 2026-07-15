package com.projetofinal.paymentservice.service;

import com.projetofinal.paymentservice.domain.Payment;
import com.projetofinal.paymentservice.event.OrderCreatedEvent;
import com.projetofinal.paymentservice.exception.PaymentNotFoundException;
import com.projetofinal.paymentservice.gateway.PaymentGatewayClient;
import com.projetofinal.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayClient gatewayClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentGatewayClient gatewayClient) {
        this.paymentRepository = paymentRepository;
        this.gatewayClient = gatewayClient;
    }

    public Payment process(OrderCreatedEvent event) {
        var resultado = gatewayClient.criarCobranca(event.descricao(), event.valor());
        Payment payment = new Payment(event.pedidoId(), event.valor(), resultado.status(), resultado.transactionId());
        return paymentRepository.save(payment);
    }

    public Payment findByPedidoId(Long pedidoId) {
        return paymentRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new PaymentNotFoundException(pedidoId));
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}
