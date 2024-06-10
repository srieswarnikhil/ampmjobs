package com.quantum.ampmjobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentJobs {

	private String jobtype_name;
	private String location_name;
	private String job_type;
	private int amount;
	private String job_status;

}
