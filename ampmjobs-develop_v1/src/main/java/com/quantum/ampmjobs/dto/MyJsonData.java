package com.quantum.ampmjobs.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyJsonData implements Serializable {

	private static final long serialVersionUID = -8723988402279161703L;

	private String nationality;
	private long nationality_id;

	private long country_id;
	private String country_name;

	private long state_id;
	private String state_name;

	private long city_id;
	private String city_name;
	private String location_id;
	private String location_name;

	private long jobtype_id;
	private String jobtype_name;
	private String jobtype_longdesc;
	private String jobtype_shortdesc;

}
