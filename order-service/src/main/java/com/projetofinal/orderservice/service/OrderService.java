package com.projetofinal.orderservice.service;

import com.projetofinal.orderservice.domain.Order;
import com.projetofinal.orderservice.dto.CreateOrderRequest;
import com.projetofinal.orderservice.kafka.OrderEventProducer;
import com.projetofinal.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    public Order create(CreateOrderRequest request) {
        // TODO (Pessoa 1): salvar o pedido via orderRepository.save(new Order(...))
        // e publicar o evento OrderCreatedEvent no Kafka via orderEventProducer.publish(...)
        throw new UnsupportedOperationException("TODO: implementar OrderService.create");
    }

    public Order findById(Long id) {
        // TODO (Pessoa 1): buscar o pedido pelo id (orderRepository.findById) e lancar
        // OrderNotFoundException se nao existir
        throw new UnsupportedOperationException("TODO: implementar OrderService.findById");
    }

    public List<Order> findAll() {
        // TODO (Pessoa 1): retornar todos os pedidos cadastrados
        throw new UnsupportedOperationException("TODO: implementar OrderService.findAll");
    }
}
