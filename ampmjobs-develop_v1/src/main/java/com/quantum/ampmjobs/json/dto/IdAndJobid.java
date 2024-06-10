package com.quantum.ampmjobs.json.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdAndJobid {
	private long id;
	private int job_id;
}
