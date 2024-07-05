package com.quantum.ampmjobs.api.payment.utility;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantData {
	private String merchantId;
	private String merchantTransactionId;
	private InstrumentResponse instrumentResponse;
}