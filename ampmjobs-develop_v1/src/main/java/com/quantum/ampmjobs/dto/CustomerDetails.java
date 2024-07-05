package com.quantum.ampmjobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetails {

	private String email;
	private long mobile;
	private String role;

	private int masterPayId;
	private long userId;

}
