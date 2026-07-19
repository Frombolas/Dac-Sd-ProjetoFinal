package com.projetofinal.paymentservice.controller;

import com.projetofinal.paymentservice.domain.Payment;
import com.projetofinal.paymentservice.domain.PaymentStatus;
import com.projetofinal.paymentservice.exception.PaymentNotFoundException;
import com.projetofinal.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void deveConsultarPagamentoPorPedido() throws Exception {
        var payment = new Payment(1L, new BigDecimal("99.90"), PaymentStatus.APROVADO, "mp-123");
        when(paymentService.findByPedidoId(1L)).thenReturn(payment);

        mockMvc.perform(get("/api/pagamentos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));
    }

    @Test
    void deveRetornar404QuandoPagamentoNaoExiste() throws Exception {
        when(paymentService.findByPedidoId(99L)).thenThrow(new PaymentNotFoundException(99L));

        mockMvc.perform(get("/api/pagamentos/pedido/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarPagamentos() throws Exception {
        when(paymentService.findAll()).thenReturn(
                List.of(new Payment(1L, new BigDecimal("10.00"), PaymentStatus.PENDENTE, "id")));

        mockMvc.perform(get("/api/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
