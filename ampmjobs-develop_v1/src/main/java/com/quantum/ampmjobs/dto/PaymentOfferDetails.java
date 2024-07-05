package com.quantum.ampmjobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOfferDetails {

	private String duration;
	private long regFee;

	private boolean isDiscountApplicable;

	private double discountPercentage;
	private long effectiveAmount;

	private int masterPaymentId;

}
