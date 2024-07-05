package com.quantum.ampmjobs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Applicants {
	private String email;
	private String skill;
	private String gender;
	private String job_status;
	private String pincode;
	private String location_name;
	private String city_name;
	private String state_name;
	private int student_id;
	private String jobtype_name;
	private int jobtype_id;
	private String address_line1;
	private String address_line2;
	private String qualification;
	private String applicant_name;
	private long appilcant_phone;
	private int employer_job_detail_id;
	private int student_job_applied;
	private int employer_id;

	private int age;
	private String jobnames;
	private String student_name;
	private String zipcode;
	private Boolean is_employer_viewed;
	private String unique_job_code;

}
