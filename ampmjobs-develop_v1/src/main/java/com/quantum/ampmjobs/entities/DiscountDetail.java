package com.quantum.ampmjobs.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_discount_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_sequence_generator")
	@SequenceGenerator(name = "my_sequence_generator", sequenceName = "payment_discount_detail_discount_detail_id_seq", allocationSize = 1)
	@Column(name = "discount_detail_id")
	private int discountDetailId;

	@Column(name = "user_id")
	private long userId;

	@Column(name = "discount_percentage")
	private double discountPercentage;

	@Column(name = "payment_lookup_id")
	private int paymentLookupId;

	@Column(name = "isactive")
	private boolean isActive;

}