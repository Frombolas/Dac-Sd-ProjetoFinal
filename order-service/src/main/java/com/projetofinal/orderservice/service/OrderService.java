package com.projetofinal.orderservice.service;

import com.projetofinal.orderservice.domain.Order;
import com.projetofinal.orderservice.dto.CreateOrderRequest;
import com.projetofinal.orderservice.event.OrderCreatedEvent;
import com.projetofinal.orderservice.exception.OrderNotFoundException;
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
        Order order = orderRepository.save(new Order(request.descricao(), request.valor()));
        orderEventProducer.publish(new OrderCreatedEvent(
                order.getId(), order.getDescricao(), order.getValor(), order.getCriadoEm()));
        return order;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
