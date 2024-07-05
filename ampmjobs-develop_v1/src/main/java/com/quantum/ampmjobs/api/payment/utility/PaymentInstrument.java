package com.quantum.ampmjobs.api.payment.utility;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstrument {
	@JsonProperty("type")
	private String type;

	@JsonProperty("cardType")
	private String cardType;

	@JsonProperty("pgTransactionId")
	private String pgTransactionId;

	@JsonProperty("pgAuthorizationCode")
	private String pgAuthorizationCode;

	@JsonProperty("pgServiceTransactionId")
	private String pgServiceTransactionId;

	@JsonProperty("bankTransactionId")
	private String bankTransactionId;

	@JsonProperty("bankId")
	private String bankId;

	@JsonProperty("arn")
	private String arn;

	@JsonProperty("brn")
	private String brn;

	@JsonProperty("utr")
	private String utr;

}
