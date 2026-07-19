package com.projetofinal.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofinal.orderservice.domain.Order;
import com.projetofinal.orderservice.dto.CreateOrderRequest;
import com.projetofinal.orderservice.exception.OrderNotFoundException;
import com.projetofinal.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        var order = new Order("Assinatura mensal", new BigDecimal("99.90"));
        when(orderService.create(any())).thenReturn(order);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateOrderRequest("Assinatura mensal", new BigDecimal("99.90")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Assinatura mensal"));
    }

    @Test
    void deveRetornar400ParaValorInvalido() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateOrderRequest("Assinatura mensal", new BigDecimal("0")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoPedidoNaoExiste() throws Exception {
        when(orderService.findById(99L)).thenThrow(new OrderNotFoundException(99L));

        mockMvc.perform(get("/api/pedidos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarPedidos() throws Exception {
        when(orderService.findAll()).thenReturn(List.of(new Order("Teste", new BigDecimal("10.00"))));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
