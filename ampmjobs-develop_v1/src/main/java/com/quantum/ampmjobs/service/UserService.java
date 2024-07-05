package com.quantum.ampmjobs.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.quantum.ampmjobs.dto.Otp;
import com.quantum.ampmjobs.dto.SiteStatistics;
import com.quantum.ampmjobs.dto.UserDetails;
import com.quantum.ampmjobs.entities.AuthorizedUser;

public interface UserService {

	int createUserAccount(long mobileNo, Integer flag);

	int validateOTP(long userMobileNo, String userOTPcode);

	int validatePassword(long userMobileNo, Integer userOTPcode, String pwd, String cnfPwd, HttpServletRequest request);

	int updateUserDetails(UserDetails userDetails);

	int resetPassword(long long1, String pwd, String cnfPwd, String oldpwd);

	int verifyUserAndGetOTP(Otp loginDetails);

	int isEmailExist(String email);

	int resetPasswordByEmail(String email, String pwd, String cnfPwd);

	void updateOTP(String code, String email);

	String getUniqueResult(String query);

	UserDetails getProfileDetails(AuthorizedUser myUser, int userType);

	List<String> getUserUnAvailabilityInfo(Long userId);

	void addNewUserUnAvailabilityInfo(Long userId, String sDate, String eDate);

	void deleteUnAvailableInfo(Long userId, String unavailable_date);

	SiteStatistics getSiteStatistics();

	boolean checkIsAddInfoAvailable(AuthorizedUser user);

}
