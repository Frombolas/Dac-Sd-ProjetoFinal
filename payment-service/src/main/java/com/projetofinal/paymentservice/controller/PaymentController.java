package com.projetofinal.paymentservice.controller;

import com.projetofinal.paymentservice.dto.PaymentResponse;
import com.projetofinal.paymentservice.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/pedido/{pedidoId}")
    public PaymentResponse findByPedidoId(@PathVariable Long pedidoId) {
        return PaymentResponse.from(paymentService.findByPedidoId(pedidoId));
    }

    @GetMapping
    public List<PaymentResponse> findAll() {
        return paymentService.findAll().stream().map(PaymentResponse::from).toList();
    }
}
