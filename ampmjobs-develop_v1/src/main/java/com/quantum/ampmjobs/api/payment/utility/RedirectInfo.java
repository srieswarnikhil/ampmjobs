package com.quantum.ampmjobs.api.payment.utility;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class RedirectInfo {
	private String url;
	private String method;
}
