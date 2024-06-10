package com.quantum.ampmjobs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quantum.ampmjobs.entities.DiscountDetail;

public interface DiscountDetailDao extends JpaRepository<DiscountDetail, Integer> {

	List<DiscountDetail> findAllByUserId(long userId);

	DiscountDetail findByUserIdAndIsActiveIsTrue(long userId);

}
