package com.quantum.ampmjobs.service.impl;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quantum.ampmjobs.api.utility.SMSUtility;
import com.quantum.ampmjobs.dao.LoginDetailsRepository;
import com.quantum.ampmjobs.dao.UserRepository;
import com.quantum.ampmjobs.dto.Otp;
import com.quantum.ampmjobs.dto.SiteStatistics;
import com.quantum.ampmjobs.dto.UserDetails;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.json.dto.MobileAndEmail;
import com.quantum.ampmjobs.json.dto.UnAvailableDates;
import com.quantum.ampmjobs.service.UserService;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordEncoder passwordencoder;

	@Autowired
	private SMSUtility smsUtility;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private LoginDetailsRepository loginDetailsRepository;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Autowired
	@Qualifier("db.props")
	private Properties messageSource;

	@Override
	public int createUserAccount(final long mobileNo, final Integer flag) {
		// 0 fail
		// 1 success
		// 2 user already exist, OTP not verified
		// 3 user already exist, email not verified
		// 4 user already exist, payment not verified
		// 5 user already exist
		int res = 0;
		LoginDetails loginDetails;

		try {
			loginDetails = loginDetailsRepository.findLoginDetailsByMobile(mobileNo);
			if (loginDetails != null && loginDetails.getPhone() == mobileNo) {
				res = !loginDetails.isMobileVerified() ? 2
						: !loginDetails.isEmailVerified() ? 3 : !loginDetails.isPaymentVerified() ? 4 : 5;
			}

			if (res == 2 || res == 0 || res == 3) {
				loginDetails = loginDetails == null ? new LoginDetails() : loginDetails;
				String otp = ActivityUtilities.generateOTP();
				loginDetails.setPhone(mobileNo);
				loginDetails.setOtp(otp);
				loginDetails.setRole(1 == flag ? "STUDENT" : "EMPLOYER");
				System.out.println(loginDetails.toString());
				System.out.println(" otp : " + otp + " and mobileNo : " + mobileNo);

				LoginDetails action = loginDetailsRepository.save(loginDetails);
				if (action != null) {
					smsUtility.sentRegistrationOtp(mobileNo, otp);
				}
				return action == null ? 0 : 1;
			}
		} catch (Exception e) {
			res = 0;
		}
		return res;
	}

	@Override
	public int validateOTP(final long userMobileNo, final String userOTPcode) {
		int res = 0;
		try {
			LoginDetails loginDetails = loginDetailsRepository.findLoginDetailsByMobile(userMobileNo);
			if (loginDetails == null) {
				return 0;
			} else if (userOTPcode.equals(loginDetails.getOtp())) {
				// if valid then is_mobile_verified=true
				loginDetails.setMobileVerified(true);
				loginDetails.setOtp(null);
				LoginDetails save = loginDetailsRepository.save(loginDetails);
				res = save.isMobileVerified() ? 1 : 0;
			}
		} catch (Exception e) {
			res = 2;
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int validatePassword(final long userMobileNo, final Integer userOTPcode, final String pwd,
			final String cnfPwd, final HttpServletRequest request) {
		// 0 == error
		// 1 == success
		// 2 == account created already

		int res = 0;
		try {
			if (pwd == null || "".equals(pwd.trim()) || cnfPwd == null || "".equals(cnfPwd.trim())
					|| !pwd.equals(cnfPwd)) {
				res = 0;
			} else {
				LoginDetails loginDetails = loginDetailsRepository.findLoginDetailsByMobile(userMobileNo);
				if (loginDetails == null) {
					res = 0;
				} else if (loginDetails.isEmailVerified()) {
					res = 2;
				} else {
					loginDetails.setEmailVerified(true);
					loginDetails.setPassword(passwordencoder.encode(cnfPwd));
					loginDetails.setOtp(null);
					loginDetails.setIpAddress(ActivityUtilities.getIPAddress(request));
					loginDetailsRepository.save(loginDetails);

					res = 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			res = 0;
		}
		return res;
	}

	@Override
	public int updateUserDetails(final UserDetails userDetails) {
		int res = 0;
		try {
			System.out.println(userDetails.toString());
			if ("1".equals(userDetails.getUserType()) || "2".equals(userDetails.getUserType())) {
				String sql = "1".equals(userDetails.getUserType()) ? "CALL public.usp_proc_student(?::jsonb,?)"
						: "CALL public.usp_proc_employer(?::jsonb,?)";
				String json = ActivityUtilities.convertObjectIntoJson(userDetails);
				String dbupdate = jdbcTemplate.queryForObject(sql, String.class, json, "");
				res = "1".equals(dbupdate) ? 1 : 0;
				System.out.println("status from DB : " + dbupdate);
			} else {
				res = 0;
			}
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int resetPassword(final long mobile, final String pwd, final String cnfPwd, final String oldPassword) {

		int res = 0; // 0 == error, 1== success, 2== password & confirm password not match,
		// 3 == user not available, 4== mobile not verified, 5 == email not verified
		// 6== current password is wrong , 7== current password is same as new password

		try {
			if (pwd == null || "".equals(pwd.trim()) || cnfPwd == null || "".equals(cnfPwd.trim())
					|| !pwd.equals(cnfPwd)) {
				res = 2;
			} else {
				LoginDetails loginDetails = loginDetailsRepository.findLoginDetailsByMobile(mobile);
				if (loginDetails == null) {
					res = 3;
				} else {
					if (!loginDetails.isMobileVerified()) {
						res = 4;
					} else if (!loginDetails.isEmailVerified()) {
						res = 5;
					}
					if (oldPassword != null && oldPassword.trim() != "") {
						boolean isCurrentPasswordMatched = passwordencoder.matches(oldPassword,
								loginDetails.getPassword());
						if (!isCurrentPasswordMatched) {
							res = 6;
						} else {
							boolean isNewPasswordSameAsOldPassword = passwordencoder.matches(pwd,
									loginDetails.getPassword());
							if (isNewPasswordSameAsOldPassword) {
								res = 7;
							}
						}
					}
					if (res == 0) {
						loginDetails.setEmailVerified(true);
						loginDetails.setPassword(passwordencoder.encode(cnfPwd));
						loginDetailsRepository.save(loginDetails);
						res = 1;
					}
				}
			}
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int verifyUserAndGetOTP(final Otp dto) {
		// 0== error, 1 = success, 2== no user,
		// 3 == email not verified,
		// 4 = wrong person i.e url modified
		int res = 0;
		try {
			LoginDetails loginDetails = loginDetailsRepository
					.findLoginDetailsByMobile(Long.parseLong(dto.getMobile()));
			if (loginDetails == null) {
				res = 2;
			} else if (!loginDetails.isEmailVerified()) {
				res = 3;
			} else if (((!"STUDENT".equals(loginDetails.getRole()) || !"1".equals(dto.getFlag()))
					&& (!"EMPLOYER".equals(loginDetails.getRole()) || !"2".equals(dto.getFlag())))) {
				res = 4;
			} else {
				String otp = ActivityUtilities.generateOTP();
				loginDetails.setOtp(otp);
				System.out.println(" otp : " + otp + " and mobileNo : " + dto.getMobile());

				LoginDetails save = loginDetailsRepository.save(loginDetails);
				res = save == null ? 0 : 1;
			}
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int isEmailExist(final String email) {
		String uniqueResult = userRepo.getUniqueResult(
				"select count(1) from public.login_detail where is_email_verified=true and email = '" + email + "'");
		return Integer.parseInt(uniqueResult) > 0 ? 5 : 0;
	}

	@Override
	public int resetPasswordByEmail(final String email, final String pwd, final String cnfPwd) {

		LoginDetails loginDetails = loginDetailsRepository.findLoginDetailsByEmail(email);
		loginDetails.setPassword(passwordencoder.encode(cnfPwd));
		loginDetails.setOtp(null);
		loginDetails.setEmailVerified(true);
		LoginDetails update = loginDetailsRepository.save(loginDetails);

		return update == null ? 0 : 1;
	}

	@Override
	public void updateOTP(final String code, final String email) {
		String sql = "UPDATE public.login_detail SET otp = ? , link_expire_time= ? WHERE email = ?";
		int rowsUpdated = jdbcTemplate.update(sql, code, new Date(), email);
		System.out.println("update OTP in DB: " + rowsUpdated);
	}

	@Override
	public String getUniqueResult(final String query) {
		return userRepo.getUniqueResult(query);
	}

	@Override
	public UserDetails getProfileDetails(final AuthorizedUser user, final int userType) {
		MobileAndEmail dto = new MobileAndEmail(user.getUsername(), user.getPhone());
		String jobsQuery = userType == 1 ? "select * from public.udfun_get_student_details(?::jsonb)"
				: "select * from public.udfun_get_employer_details(?::jsonb)";

		String json = ActivityUtilities.convertObjectIntoJson(dto);
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		UserDetails userDetails = new UserDetails();
		try {
			if (dbResponse != null) {
				List<UserDetails> userDetailsList = objectMapper.readValue(dbResponse,
						new TypeReference<List<UserDetails>>() {
						});
				userDetails = userDetailsList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userDetails;
	}

	@Override
	public List<String> getUserUnAvailabilityInfo(final Long userId) {
		return getUnAvailabilityData(userId);
	}

	private List<String> getUnAvailabilityData(final Long userId) {
		String message = messageSource.getProperty("get_student_un_available_dates");
		String sql = MessageFormat.format(message, String.valueOf(userId));
		String unavailable_dates = userRepo.getUniqueResult(sql);
		List<String> dateList = new ArrayList<>();

		if (unavailable_dates != null) {
			unavailable_dates = unavailable_dates.replaceAll("[{}]", "");

			String[] dbDates = unavailable_dates.split(",");
			for (String dbDate : dbDates) {
				if (dbDate != null && !"".equals(dbDate.trim())) {
					dateList.add(dbDate);
				}
			}
		}

		return dateList;

	}

	@Override
	public void addNewUserUnAvailabilityInfo(final Long userId, final String sDate, final String eDate) {

		try {
			List<String> dbDates = getUnAvailabilityData(userId);

			List<LocalDate> updatedDates = dbDates.stream().map(date -> LocalDate.parse(date, dateFormatter))
					.collect(Collectors.toList());

			LocalDate fromDate = LocalDate.parse(sDate, dateFormatter);
			LocalDate endDate = LocalDate.parse(eDate, dateFormatter);
			LocalDate currentDate = fromDate;
			while (!currentDate.isAfter(endDate)) {
				if (!updatedDates.contains(currentDate)) {
					updatedDates.add(currentDate);
				}
				currentDate = currentDate.plusDays(1);
			}

			updateUnAvailablity(userId, updatedDates);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void updateUnAvailablity(final Long userId, List<LocalDate> finalDates) {
		try {

			finalDates = finalDates.stream().filter(date -> date.isAfter(LocalDate.now())).collect(Collectors.toList());

			Collections.sort(finalDates);

			UnAvailableDates dto = new UnAvailableDates(userId, finalDates);
			String sql_update = "CALL public.usp_proc_student_unavailable_date(?::jsonb,?)";
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			String myJson = mapper.writeValueAsString(dto);

			String dbResponse = jdbcTemplate.queryForObject(sql_update, String.class, myJson, "");
			System.out.println("updating UnAvailablity dbResponse : " + dbResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void deleteUnAvailableInfo(final Long userId, final String unavailable_date) {
		List<LocalDate> finalDates = new ArrayList<>();
		String message = messageSource.getProperty("get_student_un_available_dates");
		String sql = MessageFormat.format(message, String.valueOf(userId));
		String unavailable_dates = userRepo.getUniqueResult(sql);

		if (unavailable_dates != null) {
			unavailable_dates = unavailable_dates.replaceAll("[{}]", "");

			String[] dbDates = unavailable_dates.split(",");
			LocalDate.now();
			for (String dbDate : dbDates) {
				if (!unavailable_date.equals(dbDate) && dbDate != "" && unavailable_date != null) {
					finalDates.add(LocalDate.parse(dbDate, dateFormatter));
				}
			}
		}

		updateUnAvailablity(userId, finalDates);
	}

	@Override
	public SiteStatistics getSiteStatistics() {

		String jobsQuery = "select * from udfun_get_ampmjobs_statistics()";

		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SiteStatistics res = new SiteStatistics();
		try {
			res = objectMapper.readValue(dbResponse, SiteStatistics.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return res;

	}

	@Override
	public boolean checkIsAddInfoAvailable(final AuthorizedUser user) {
		String message = messageSource.getProperty("get_user_city");
		String sql = MessageFormat.format(message, user.getRole(), "'" + user.getUsername() + "'",
				String.valueOf(user.getPhone()));
		String cityid = userRepo.getUniqueResult(sql);
		return cityid == null ? false : Integer.valueOf(cityid) > 0 ? true : false;
	}

}
