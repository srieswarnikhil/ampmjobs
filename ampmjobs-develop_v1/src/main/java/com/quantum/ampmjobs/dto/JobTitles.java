package com.quantum.ampmjobs.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTitles implements Serializable {

	private static final long serialVersionUID = -5195400975823274352L;
	private int jobtype_id;
	private String jobtype_name;
	private long matched_student_count;
}
