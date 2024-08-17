/*package com.quantum.ampmjobs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantum.ampmjobs.dao.UserRepository;
import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.JobTitles;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.json.dto.JobApply;
import com.quantum.ampmjobs.json.dto.SearchJob;
import com.quantum.ampmjobs.json.dto.UserIdAndFlag;
import com.quantum.ampmjobs.service.StudentService;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int getCountryId(final String email) {
		String sql = "select country_id from public.student where email = '" + email + "' limit 1";
		String uniqueResult = userRepo.getUniqueResult(sql);
		return Integer.parseInt(uniqueResult);
	}

	@Override
	public List<JobDetails> getPreferredJobs(final AuthorizedUser user) {
		String sql = "select id from public.student where email = '" + user.getUsername() + "' and phone = "
				+ user.getPhone() + " limit 1";
		String studentId = userRepo.getUniqueResult(sql);
		String jobsQuery = "select * from public.udfun_get_student_dashboard_jobdetails(" + studentId + ")";
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		List<JobDetails> jobDetails = new ArrayList<>();
		if (dbResponse != null) {
			try {
				jobDetails = objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return jobDetails;

	}

	@Override
	public List<JobTitles> getPreferredJobTitles(final AuthorizedUser user) {
		UserIdAndFlag in = new UserIdAndFlag(user.getUserId(), "student");
		String jobsQuery = "select * from public.udfun_get_preferred_job_titles(?::jsonb)";
		String json = ActivityUtilities.convertObjectIntoJson(in);
		String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		List<JobTitles> jobTitlesDtoList = new ArrayList<>();
		if (dbResponse != null) {
			try {
				jobTitlesDtoList = objectMapper.readValue(dbResponse, new TypeReference<List<JobTitles>>() {
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jobTitlesDtoList;
	}

	@Override
	public Map<String, List<JobDetails>> getMappedJobs(final AuthorizedUser user) {
		List<JobTitles> preferredJobTitles = getPreferredJobTitles(user);
		List<JobDetails> preferredJobs = getPreferredJobs(user);
		if (preferredJobs != null) {
			Map<String, List<JobDetails>> map = preferredJobTitles.stream().collect(Collectors.toMap(
					JobTitles::getJobtype_name,
					jobTitle -> preferredJobs.stream().filter(job -> job.getJobtype_id() == jobTitle.getJobtype_id())
							.collect(Collectors.toList()),
					(jobDetailsList1, jobDetailsList2) -> jobDetailsList1, LinkedHashMap::new));
			return map;
		} else {
			Map<String, List<JobDetails>> map = new HashMap<>();
			preferredJobTitles.stream().forEach(title -> {
				map.put(title.getJobtype_name(), new ArrayList<>());
			});
			return map;
		}
	}

	@Override
	public int applyJob(final int jobId, final AuthorizedUser user) {
		int res = 0;
		try {
			JobApply dto = new JobApply(jobId, user.getUserId(), false, false, "Applied");
			String sql = "CALL public.usp_proc_job_application_request(?::jsonb,?)";
			String json = ActivityUtilities.convertObjectIntoJson(dto);
			String dbupdate = jdbcTemplate.queryForObject(sql, String.class, json, "");
			res = Integer.parseInt(dbupdate);
			System.out.println("job apply by student, status from DB : " + dbupdate);
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;

	}

	@Override
	public List<JobDetails> getAppliedJobs(final AuthorizedUser user) {
		List<JobDetails> jobs = new ArrayList<>();
		try {
			SearchJob dto = new SearchJob(user.getUserId(), 1, 1000);

			String jobsQuery = "select * from public.udfun_get_student_job_applied(?::jsonb)";
			String json = ActivityUtilities.convertObjectIntoJson(dto);
			String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (dbResponse != null) {
				jobs = objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobs;
	}

	@Override
	public List<JobDetails> getAllJobsBySearch(final AuthorizedUser user) {

		List<JobDetails> jobs = new ArrayList<>();
		try {
			SearchJob dto = new SearchJob(user.getUserId(), 1, 500);

			String jobsQuery = "select * from public.udfun_get_student_dashboard_jobdetails_search(?::jsonb)";
			String json = ActivityUtilities.convertObjectIntoJson(dto);
			String dbResponse = jdbcTemplate.queryForObject(jobsQuery, String.class, json);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if (dbResponse != null) {
				jobs = objectMapper.readValue(dbResponse, new TypeReference<List<JobDetails>>() {
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobs;

	}

}*/


package com.quantum.ampmjobs.controller.student;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.quantum.ampmjobs.api.payment.utility.ApiResponse;
import com.quantum.ampmjobs.api.payment.utility.DiscountDetails;
import com.quantum.ampmjobs.api.payment.utility.PaymentApi;
import com.quantum.ampmjobs.api.payment.utility.PaymentReCheckResponse;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.PaymentOfferDetails;
import com.quantum.ampmjobs.dto.UserDetails;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.entities.PaymentMaster;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.service.StudentService;
import com.quantum.ampmjobs.service.UserService;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Value("${lu.db.property.nationality}")
	private String nationality;

	@Value("${lu.db.property.state}")
	private String StateProperty;

	@Value("${lu.db.property.job}")
	private String jobProperty;

	@Value("${profile.photos.upload.dir}")
	private String PHOTO_LOCATION;

	@Value("${applicationUrl}")
	private String applicationUrl;

	@Autowired
	private PublicService publicService;

	@Autowired
	private StudentService studentService;

	@Autowired
	private UserService userService;

	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

	@GetMapping("/getPaymentInfo")
	public String getPaymentInfo(@RequestParam(required = false) final String error, final Model model,
	        @AuthenticationPrincipal final AuthorizedUser user) {
	    model.addAttribute("isError", error);
	    List<PaymentMaster> defaultPayments = new ArrayList<>();
	    PaymentOfferDetails pod = new PaymentOfferDetails();
	    pod.setDiscountApplicable(false);
	    if (error == null) {
	        defaultPayments = publicService.getDefaultPayments("Student");

	        // load student payment details & show to student & do the discount calculations
	        // also
	        pod = publicService.getEffectiveDiscountDetails(user.getUsername(), user.getPhone(), pod);

	    }
	    model.addAttribute("defaultPayments", defaultPayments);
	    model.addAttribute("pod", pod);

	    return getAddInfo(model, user); // Call getAddInfo instead of returning "student/additional-info"
	}
	
	
//	@GetMapping("/getPaymentInfo")
//	public String getPaymentInfo(@RequestParam(required = false) final String error, final Model model,
//			@AuthenticationPrincipal final AuthorizedUser user) {
//		model.addAttribute("isError", error);
//		List<PaymentMaster> defaultPayments = new ArrayList<>();
//		PaymentOfferDetails pod = new PaymentOfferDetails();
//		pod.setDiscountApplicable(false);
//		if (error == null) {
//			defaultPayments = publicService.getDefaultPayments("Student");
//
//			// load student payment details & show to student & do the discount calculations
//			// also
//			pod = publicService.getEffectiveDiscountDetails(user.getUsername(), user.getPhone(), pod);
//
//		}
//		model.addAttribute("defaultPayments", defaultPayments);
//		model.addAttribute("pod", pod);
//
//		return "student/payment";
//	}

	@Autowired
	private PaymentApi api;

	@GetMapping("/validatePayment")
	public RedirectView validatePayment(@AuthenticationPrincipal final AuthorizedUser user,
			@RequestParam final String payId) {

		String url = applicationUrl + "/student/getPaymentInfo?error=error";
		try {
			DiscountDetails discountDetails = new DiscountDetails();
			discountDetails.setMerchantMobileNo("" + user.getPhone());
			discountDetails.setMerchantTransactionId(publicService.generateMtrId());
			discountDetails.setEmail(user.getUsername());
			discountDetails.setPhone(user.getPhone());
			ApiResponse pay = api.payByStudent(discountDetails, payId);
			if (pay.isSuccess()) {
				url = pay.getData().getInstrumentResponse().getRedirectInfo().getUrl();
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new RedirectView(url);
	}
	
	@GetMapping("/phonePayToAmPm")
	public RedirectView phonePayToAmPm(@AuthenticationPrincipal final AuthorizedUser user) {
	    String url = applicationUrl + "/student/updatePayInDB"; // Always go to updatePayInDB
	    try {
	        PaymentReCheckResponse checkPaymentStatus = api.checkPaymentStatus(user.getUsername(), user.getPhone());
	        if (checkPaymentStatus.isSuccess()) {
	            url = applicationUrl + "/student/updatePayInDB";
	        }
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return new RedirectView(url);
	}

	@GetMapping("/updatePayInDB")
	public RedirectView updatePayInDB(@AuthenticationPrincipal final AuthorizedUser user) {
	    try {
	        if (!user.isPaymentCompleted()) {
	            LoginDetails details = publicService.findLoginDetailsByEmail(user.getUsername());
	            details.setPaymentVerified(true);
	            details.setPaymentExpireDate(
	                    publicService.generatePaymentExpireDate(user.getUsername(), user.getPhone()));
	            publicService.updateLoginDetails(details);
	            AuthorizedUser updatedUserDetails = user;
	            updatedUserDetails.setPaymentCompleted(true);
	            updatedUserDetails.setPaymentExpireDate(dateFormatter.format(details.getPaymentExpireDate()));
	            Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
	            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUserDetails,
	                    currentAuthentication.getCredentials(), currentAuthentication.getAuthorities());
	            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return new RedirectView(applicationUrl + "/student/getAddInfo"); // Always redirect to getAddInfo
	}

	
	
/*
	@GetMapping("/phonePayToAmPm")
	public RedirectView phonePayToAmPm(@AuthenticationPrincipal final AuthorizedUser user) {

		String url = applicationUrl + "/student/getPaymentInfo?error=error";

		try {
			PaymentReCheckResponse checkPaymentStatus = api.checkPaymentStatus(user.getUsername(), user.getPhone());
			if (checkPaymentStatus.isSuccess()) {
				url = applicationUrl + "/student/updatePayInDB";
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new RedirectView(url);
	}

	@GetMapping("/updatePayInDB")
	public RedirectView updatePayInDB(@AuthenticationPrincipal final AuthorizedUser user) {

		String url = applicationUrl + "/student/getPaymentInfo?error=error";

		try {
			if (!user.isPaymentCompleted()) {
				LoginDetails details = publicService.findLoginDetailsByEmail(user.getUsername());
				details.setPaymentVerified(true);
				details.setPaymentExpireDate(
						publicService.generatePaymentExpireDate(user.getUsername(), user.getPhone()));
				publicService.updateLoginDetails(details);
				AuthorizedUser updatedUserDetails = user;
				updatedUserDetails.setPaymentCompleted(true);
				updatedUserDetails.setPaymentExpireDate(dateFormatter.format(details.getPaymentExpireDate()));
				Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
				Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUserDetails,
						currentAuthentication.getCredentials(), currentAuthentication.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);

				url = applicationUrl + "/student/getAddInfo";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new RedirectView(url);
	}
*/
	@GetMapping("/getAddInfo")
	public String getAddInfo(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {

		if (user.isAddtionDetailsFilled()) {
			return "redirect:/user/home";
		} else {

			int countryId = studentService.getCountryId(user.getUsername());
			List<MyJsonData> stateData = publicService.getCommonData(StateProperty, countryId);
			model.addAttribute("stateList", stateData);
			List<MyJsonData> jobsData = publicService.getCommonData(jobProperty, 0);
			model.addAttribute("jobsData", jobsData);
			return "student/additional-info";
		}

	}

	@PostMapping("/updateRemInfo")
	@ResponseBody
	public int updateRemInfo(@RequestBody final UserDetails userDetails,
			@AuthenticationPrincipal final AuthorizedUser user) {
		int updateStatus = 0;
		try {
			LoginDetails details = publicService.findLoginDetailsByEmail(user.getUsername());

			userDetails.setUserType("1");
			userDetails.setFlag("remaining_details");
			userDetails.setPhone(String.valueOf(details.getPhone()));
			userDetails.setEmail(details.getEmail());
			updateStatus = userService.updateUserDetails(userDetails);
			if (updateStatus > 0 && !user.isAddtionDetailsFilled()) {
				AuthorizedUser updatedUserDetails = user;
				updatedUserDetails.setAddtionDetailsFilled(true);
				Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
				Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUserDetails,
						currentAuthentication.getCredentials(), currentAuthentication.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			}
		} catch (Exception e) {
			updateStatus = 0;
			e.printStackTrace();
		}
		return updateStatus;
	}

	@GetMapping("/getUnAvailableInfo")
	public String getUnAvailableData(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {
		model.addAttribute("dateList", userService.getUserUnAvailabilityInfo(user.getUserId()));
		return "student-un-availability";
	}

	@GetMapping("/addNewUnAvailableInfo/{sDate}/{eDate}")
	public String addNewUnAvailableData(final Model model, @AuthenticationPrincipal final AuthorizedUser user,
			@PathVariable final String sDate, @PathVariable final String eDate) {
		userService.addNewUserUnAvailabilityInfo(user.getUserId(), sDate, eDate);
		return "redirect:/student/getUnAvailableInfo";
	}

	@GetMapping("/deleteUnAvailableInfo/{unavailable_date}")
	public String deleteUnAvailableData(final Model model, @AuthenticationPrincipal final AuthorizedUser user,
			@PathVariable final String unavailable_date) {
		try {
			String dd = URLDecoder.decode(unavailable_date, "UTF-8");
			userService.deleteUnAvailableInfo(user.getUserId(), dd);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return "redirect:/student/getUnAvailableInfo";
	}

	@PostMapping("/applyJob/{jobId}")
	@ResponseBody
	public int applyJob(@PathVariable final int jobId, @AuthenticationPrincipal final AuthorizedUser user) {

		return studentService.applyJob(jobId, user);

	}

	@PostMapping("/updateStudentProfile")
	@ResponseBody
	public int updateStuProfile(@RequestParam("qualification") final String qualification,
			@RequestParam("address1") final String address1, @RequestParam("address2") final String address2,
			@RequestParam("state") final String state, @RequestParam("city") final String city,
			@RequestParam("zipcode") final String zipcode, @RequestParam("location") final String location,
			@RequestPart(name = "imageFile", required = false) final MultipartFile file,
			@AuthenticationPrincipal final AuthorizedUser user) {
		int res = 0;
		try {
			String newName = "";
			UserDetails userDetails = new UserDetails();
			if (file != null) {
				String extension = "";
				int dotIndex = file.getOriginalFilename().lastIndexOf(".");
				if (dotIndex > 0 && dotIndex < file.getOriginalFilename().length() - 1) {
					extension = file.getOriginalFilename().substring(dotIndex + 1);
				}
				newName = "Student_" + user.getPhone() + "_profile." + extension;
				byte[] bytes = file.getBytes();
				Path path = Paths.get(PHOTO_LOCATION + newName);
				Files.write(path, bytes);
				userDetails.setPhoto_path(path.toString());
			}
			userDetails.setQualification(qualification);
			userDetails.setAddress_line1(address1);
			userDetails.setAddress_line2(address2);
			userDetails.setState_id(state);
			userDetails.setCity_id(city);
			userDetails.setLocation(location);
			userDetails.setZipcode(zipcode);
			userDetails.setFlag("edit_profile");
			userDetails.setUserType("1");
			userDetails.setEmail(user.getUsername());
			userDetails.setPhone(String.valueOf(user.getPhone()));
			res = userService.updateUserDetails(userDetails);
			if (res > 0 && file != null && !newName.equals(user.getPhotoPath())) {
				AuthorizedUser updatedUserDetails = user;
				updatedUserDetails.setPhotoPath(newName);
				Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
				Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUserDetails,
						currentAuthentication.getCredentials(), currentAuthentication.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			}

		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;
	}

}

