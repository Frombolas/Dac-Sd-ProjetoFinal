package com.projetofinal.paymentservice.kafka;

import com.projetofinal.paymentservice.event.OrderCreatedEvent;
import com.projetofinal.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${app.kafka.topic.pedidos}", groupId = "payment-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        var payment = paymentService.process(event);
        log.info("Pedido {} processado com status {}", event.pedidoId(), payment.getStatus());
    }
}
