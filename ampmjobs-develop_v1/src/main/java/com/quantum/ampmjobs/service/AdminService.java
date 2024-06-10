package com.quantum.ampmjobs.service;

import java.util.List;

import com.quantum.ampmjobs.dto.CustomerDetails;
import com.quantum.ampmjobs.entities.DiscountDetail;

public interface AdminService {

	CustomerDetails fetchUserDetails(String email, String phone);

	int updateUserDiscount(String discount, String userId, String masterPayId);

	String getDidcountDetails(String email, String phone);

	DiscountDetail getDidcountDetailsByUserId(long userId);

	List<String> getUserDetails(String inData);

}
