package com.quantum.ampmjobs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quantum.ampmjobs.entities.PaymentMaster;

public interface PaymentMasterDao extends JpaRepository<PaymentMaster, Integer> {

	List<PaymentMaster> findByPaymentCategoryAndIsActiveTrue(String paymentCategory);

}
