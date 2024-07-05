package com.quantum.ampmjobs.dto;

import java.util.List;

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
public class UserDetails {

	private String name;
	private String email;
	private String countryCode;
	private String phone;
	private String dob;
	private String gender;
	private String nationality;

	private String how_do_know_about;

	private String terms;
	private String flag;

	private String current_address;
	private String permenent_address;
	private String pincode;

	private String address_line1;
	private String address_line2;
	private String zipcode;

	private String country_id;
	private String state_id;
	private String city_id;

	private String institute;

	private String blood_group;
	private String qualification;
	private String location;
	private int location_id;

	private List<Integer> jobtype_id;

	private String company_name;
	private String type;
	private String gstno;
	private String business_domain;

	private String userType;

	private String photo_path;

	private int stateId;

}
