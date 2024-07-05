package com.quantum.ampmjobs.json.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdAndFlag {

	private long id;
	private String flag;

}
