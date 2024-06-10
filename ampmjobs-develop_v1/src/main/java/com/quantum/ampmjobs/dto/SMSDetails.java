package com.quantum.ampmjobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSDetails {

	private String filetype;
	private String[] msisdn;
	private String language;
	private String credittype;
	private String senderid;
	private String templateid;
	private String message;
	private String ukey;
}
