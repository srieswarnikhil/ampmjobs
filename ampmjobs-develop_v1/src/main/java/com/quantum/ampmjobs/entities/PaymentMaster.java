package com.quantum.ampmjobs.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_lookup")
@Entity
public class PaymentMaster {

	@Id
	@Column(name = "payment_lookup_id")
	private int paymentLookupId;

	@Column(name = "payment_category")
	private String paymentCategory;

	@Column(name = "payment_type")
	private String paymentType;

	@Column(name = "payment_amount")
	private Integer paymentAmount;

	@Column(name = "isactive")
	private boolean isActive;

	@Transient
	private String PaymentAmountText;
}
