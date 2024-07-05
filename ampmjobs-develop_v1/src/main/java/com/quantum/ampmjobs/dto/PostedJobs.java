package com.quantum.ampmjobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostedJobs {

	private long amount;
	private String job_type;
	private long employer_id;
	private String jobtype_name;
	private String job_description;
	private long employer_job_detail_id;
	private int total_positions;
	private int filled_positions;
	private int ramining_positions;
	private String unique_job_code;
}
