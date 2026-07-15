package com.projetofinal.paymentservice.service;

import com.projetofinal.paymentservice.domain.Payment;
import com.projetofinal.paymentservice.event.OrderCreatedEvent;
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
        // TODO (Pessoa 2): chamar gatewayClient.criarCobranca(descricao, valor), montar um Payment
        // com o resultado e salvar via paymentRepository.save(...)
        throw new UnsupportedOperationException("TODO: implementar PaymentService.process");
    }

    public Payment findByPedidoId(Long pedidoId) {
        // TODO (Pessoa 2): buscar o pagamento pelo pedidoId (paymentRepository.findByPedidoId)
        // e lancar PaymentNotFoundException se nao existir
        throw new UnsupportedOperationException("TODO: implementar PaymentService.findByPedidoId");
    }

    public List<Payment> findAll() {
        // TODO (Pessoa 2): retornar todos os pagamentos
        throw new UnsupportedOperationException("TODO: implementar PaymentService.findAll");
    }
}
