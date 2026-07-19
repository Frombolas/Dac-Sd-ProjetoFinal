package com.projetofinal.orderservice.service;

import com.projetofinal.orderservice.domain.Order;
import com.projetofinal.orderservice.domain.OrderStatus;
import com.projetofinal.orderservice.dto.CreateOrderRequest;
import com.projetofinal.orderservice.exception.OrderNotFoundException;
import com.projetofinal.orderservice.kafka.OrderEventProducer;
import com.projetofinal.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, orderEventProducer);
    }

    @Test
    void deveCriarPedidoEPublicarEventoNoKafka() {
        var request = new CreateOrderRequest("Assinatura mensal", new BigDecimal("99.90"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order resultado = orderService.create(request);

        assertThat(resultado.getDescricao()).isEqualTo("Assinatura mensal");
        assertThat(resultado.getStatus()).isEqualTo(OrderStatus.PAGAMENTO_ENVIADO);
        verify(orderEventProducer).publish(any());
        verify(orderRepository, times(2)).save(any(Order.class));
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExiste() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findById(1L));
    }

    @Test
    void deveListarTodosOsPedidos() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order("Teste", new BigDecimal("10.00"))));

        List<Order> pedidos = orderService.findAll();

        assertThat(pedidos).hasSize(1);
    }
}
