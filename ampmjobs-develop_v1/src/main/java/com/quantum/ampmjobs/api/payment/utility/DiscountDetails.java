package com.quantum.ampmjobs.api.payment.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDetails {

	private String merchantUserId;
	private String merchantMobileNo;
	private String merchantTransactionId;
	private String email;
	private long phone;

}
