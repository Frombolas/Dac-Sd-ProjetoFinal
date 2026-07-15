package com.projetofinal.paymentservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pagamentos")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String gatewayTransactionId;

    @Column(nullable = false, updatable = false)
    private Instant criadoEm;

    protected Payment() {
    }

    public Payment(Long pedidoId, BigDecimal valor, PaymentStatus status, String gatewayTransactionId) {
        this.pedidoId = pedidoId;
        this.valor = valor;
        this.status = status;
        this.gatewayTransactionId = gatewayTransactionId;
        this.criadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
