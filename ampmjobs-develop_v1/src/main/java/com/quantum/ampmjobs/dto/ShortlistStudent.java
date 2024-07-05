package com.quantum.ampmjobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortlistStudent {

	private long employer_id;
	private long student_id;
	private int jobtype_id;
	private boolean is_employer_viewed;
	private String job_status;
}
