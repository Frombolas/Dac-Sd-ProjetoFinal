package com.projetofinal.paymentservice.kafka;

import com.projetofinal.paymentservice.domain.Payment;
import com.projetofinal.paymentservice.event.OrderCreatedEvent;
import com.projetofinal.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${app.kafka.topic.pedidos}", groupId = "payment-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Recebido evento de criação de pedido: ID {}", event.pedidoId());
        try {
            Payment payment = paymentService.process(event);
            log.info("Pagamento do pedido {} processado com sucesso. Status final: {}",
                    event.pedidoId(), payment.getStatus());
        } catch (Exception e) {
            log.error("Falha ao processar pagamento do pedido {}: {}", event.pedidoId(), e.getMessage());
        }
    }
}
