package com.quantum.ampmjobs.json.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchJob {

	private long student_id;
	private int page_number;
	private int page_size;
}
