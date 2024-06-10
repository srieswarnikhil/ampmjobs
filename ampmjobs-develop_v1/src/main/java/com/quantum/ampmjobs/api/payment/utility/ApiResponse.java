package com.quantum.ampmjobs.api.payment.utility;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
	private boolean success;
	private String code;
	private String message;
	@JsonProperty("data")
	private MerchantData data;
}