package com.projetofinal.paymentservice.repository;

import com.projetofinal.paymentservice.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPedidoId(Long pedidoId);
}
