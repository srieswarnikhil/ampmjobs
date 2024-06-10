package com.quantum.ampmjobs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties
public class JobDetails {

	private int employer_job_detail_id;
	private long employer_id;
	private int jobtype_id;
	private String job_type;
	private String contact_number;
	private String contact_name;
	private int location;
	private String gender;
	private String payment_method;
	private int amount;
	private int total_positions;
	private String qualification;
	private String job_start_date;
	private String job_end_date;
	private String job_visible_date; // :"2024-03-12",
	private String job_start_time; // 10AM
	private String jobStartTimePart1;
	private String jobStartTimePart2;
	private String job_end_time; // 11 AM
	private String jobEndTimePart1;
	private String jobEndTimePart2;
	private String job_description;

	private String company_name;
	private String location_name;
	private String job_status;

	private String jobtype_name;
	private boolean is_employer_viewed;
	private boolean direct_shortlisted;

	private String unique_job_code;

}
