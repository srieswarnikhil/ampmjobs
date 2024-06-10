package com.quantum.ampmjobs.json.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApply {

	private int employer_job_detail_id;
	private long student_id;
	@JsonProperty("is_employer_viewed")
	private boolean is_employer_viewed;
	@JsonProperty("is_directly_shortlist")
	private boolean is_directly_shortlist;
	private String job_status;
}
