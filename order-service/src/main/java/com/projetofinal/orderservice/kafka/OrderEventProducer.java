package com.projetofinal.orderservice.kafka;

import com.projetofinal.orderservice.event.OrderCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String topic;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
                               @Value("${app.kafka.topic.pedidos}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(OrderCreatedEvent event) {
        // TODO (Pessoa 1): publicar o evento no topico Kafka, ex: kafkaTemplate.send(topic, chave, event)
        throw new UnsupportedOperationException("TODO: implementar OrderEventProducer.publish");
    }
}
