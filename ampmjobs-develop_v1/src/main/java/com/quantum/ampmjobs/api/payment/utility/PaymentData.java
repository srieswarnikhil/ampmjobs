package com.quantum.ampmjobs.api.payment.utility;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentData {
	@JsonProperty("merchantId")
	private String merchantId;

	@JsonProperty("merchantTransactionId")
	private String merchantTransactionId;

	@JsonProperty("transactionId")
	private String transactionId;

	@JsonProperty("amount")
	private int amount;

	@JsonProperty("state")
	private String state;

	@JsonProperty("responseCode")
	private String responseCode;

	@JsonProperty("responseCodeDescription")
	private String responseCodeDescription;

	@JsonProperty("paymentInstrument")
	private PaymentInstrument paymentInstrument;

}
