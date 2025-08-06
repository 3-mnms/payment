package com.teckit.payment.repository;

import com.teckit.payment.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent,String> {


}
