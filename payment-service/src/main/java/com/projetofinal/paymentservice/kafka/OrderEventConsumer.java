package com.projetofinal.paymentservice.kafka;

import com.projetofinal.paymentservice.event.OrderCreatedEvent;
import com.projetofinal.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${app.kafka.topic.pedidos}", groupId = "payment-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        // TODO (Pessoa 2): chamar paymentService.process(event) e logar o resultado (status do pagamento)
        throw new UnsupportedOperationException("TODO: implementar OrderEventConsumer.onOrderCreated");
    }
}
