package com.quantum.ampmjobs.json.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerPostedJobs {
	private String searchkeyword;
	private long employer_id;
	private int page_number;
	private int page_size;

}
