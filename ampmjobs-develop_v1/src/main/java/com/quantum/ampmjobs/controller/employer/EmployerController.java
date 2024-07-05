package com.quantum.ampmjobs.controller.employer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import com.quantum.ampmjobs.dao.UserRepository;
import com.quantum.ampmjobs.dto.Applicants;
import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.JobFilledData;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.PaymentOfferDetails;
import com.quantum.ampmjobs.dto.ShortlistStudent;
import com.quantum.ampmjobs.dto.UserDetails;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.entities.PaymentMaster;
import com.quantum.ampmjobs.service.EmployerService;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.service.UserService;

@Controller
@RequestMapping("/employer")
public class EmployerController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private EmployerService employerService;

	@Autowired
	private PublicService publicService;

	@Autowired
	private UserService userService;

	@Value("${applicationUrl}")
	private String applicationUrl;

	@Value("${lu.db.property.state}")
	private String StateProperty;

	@Value("${lu.db.property.job}")
	private String jobProperty;

	@Value("${profile.photos.upload.dir}")
	private String PHOTO_LOCATION;

	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

	@GetMapping("/loadResultsByJobTypeId/{jobTypeId}")
	public String loadResults(@PathVariable final int jobTypeId, @AuthenticationPrincipal final AuthorizedUser user,
			final Model model) {
		List<Applicants> students = employerService.getStudentsByJobType(jobTypeId, user.getUserId());
		model.addAttribute("students", students);

		List<JobDetails> jobUniqueCodeList = employerService.getJobUniqueCodes(jobTypeId, user.getUserId());
		model.addAttribute("UniqueCodes", jobUniqueCodeList);
		return "employer/dashboard-student-sub-grid";
	}

	@ResponseBody
	@PostMapping("/shortListStudent")
	public int modifyFilledPositions(@RequestBody final ShortlistStudent dto,
			@AuthenticationPrincipal final AuthorizedUser user) {
		dto.setEmployer_id(user.getUserId());
		dto.setJob_status("Short-Listed");
		return employerService.updateStudentShortlist(dto);
	}

	@ResponseBody
	@PostMapping("/checkIsStudentSelected")
	public boolean checkIsStudentSelected(@RequestBody final ShortlistStudent dto,
			@AuthenticationPrincipal final AuthorizedUser user) {
		dto.setEmployer_id(user.getUserId());
		return employerService.checkIsStudentSelected(dto);
	}

	@GetMapping("/getPaymentInfo")
	public String getPaymentInfo(@RequestParam(required = false) final String error, final Model model,
			@AuthenticationPrincipal final AuthorizedUser user) {

		String query = "select type from public.employer where email='" + user.getUsername() + "' limit 1";
		String companyType = userRepo.getUniqueResult(query);
		model.addAttribute("companyType", companyType);

		model.addAttribute("isError", error);
		List<PaymentMaster> defaultPayments = new ArrayList<>();
		PaymentOfferDetails pod = new PaymentOfferDetails();
		pod.setDiscountApplicable(false);
		if (error == null) {
			defaultPayments = publicService.getDefaultPayments("Employer");
			pod = publicService.getEffectiveDiscountDetails(user.getUsername(), user.getPhone(), pod);

		}
		model.addAttribute("masterPayList", defaultPayments);
		model.addAttribute("pod", pod);

		return "employer/payment";
	}

	@Autowired
	private PaymentApi api;

	@GetMapping("/validatePayment/{payId}")
	public RedirectView validatePayment(@PathVariable final String payId,
			@AuthenticationPrincipal final AuthorizedUser user) {
		String url = applicationUrl + "/employer/getPaymentInfo?error=error";
		try {
			DiscountDetails discountDetails = new DiscountDetails();
			discountDetails.setMerchantMobileNo("" + user.getPhone());
			discountDetails.setMerchantTransactionId(publicService.generateMtrId());
			discountDetails.setEmail(user.getUsername());
			discountDetails.setPhone(user.getPhone());
			ApiResponse pay = api.payFromEmployer(discountDetails, payId);
			if (pay.isSuccess()) {
				url = pay.getData().getInstrumentResponse().getRedirectInfo().getUrl();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new RedirectView(url);
	}

	@GetMapping("/phonePayToAmPm")
	public RedirectView phonePayToAmPm(@AuthenticationPrincipal final AuthorizedUser user) {

		String url = applicationUrl + "/employer/getPaymentInfo?error=error";

		try {
			PaymentReCheckResponse checkPaymentStatus = api.checkPaymentStatus(user.getUsername(), user.getPhone());
			if (checkPaymentStatus.isSuccess()) {
				url = applicationUrl + "/employer/updatePayInDB";
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new RedirectView(url);
	}

	@GetMapping("/updatePayInDB")
	public RedirectView updatePayInDB(@AuthenticationPrincipal final AuthorizedUser user) {

		String url = applicationUrl + "/employer/getPaymentInfo?error=error";

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

				url = applicationUrl + "/employer/getAddInfo";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new RedirectView(url);
	}

	@GetMapping("/getAddInfo")
	public String erAddInfo(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {

		if (!user.isPaymentCompleted()) {
			LoginDetails details = publicService.findLoginDetailsByEmail(user.getUsername());
			details.setPaymentVerified(true);
			details.setPaymentExpireDate(publicService.generatePaymentExpireDate(user.getUsername(), user.getPhone()));
			publicService.updateLoginDetails(details);

			AuthorizedUser updatedUserDetails = user;
			updatedUserDetails.setPaymentCompleted(true);
			Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
			Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUserDetails,
					currentAuthentication.getCredentials(), currentAuthentication.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		}

		if (user.isAddtionDetailsFilled()) {
			return "redirect:/user/home";
		} else {

			List<MyJsonData> stateData = publicService.getCommonData(StateProperty, 77);
			model.addAttribute("stateList", stateData);
			List<MyJsonData> jobsData = publicService.getCommonData(jobProperty, 0);
			model.addAttribute("jobsData", jobsData);
			return "employer/additional-info";
		}
	}

	@PostMapping("/updateAddInfo")
	@ResponseBody
	public int updateAddInfo(@RequestBody final UserDetails userDetails,
			@AuthenticationPrincipal final AuthorizedUser user) {
		LoginDetails details = publicService.findLoginDetailsByEmail(user.getUsername());
		userDetails.setUserType("2");
		userDetails.setFlag("remaining_details");
		userDetails.setPhone(String.valueOf(details.getPhone()));
		userDetails.setEmail(details.getEmail());

		int updateStatus = userService.updateUserDetails(userDetails);
		if (updateStatus > 0 && !user.isAddtionDetailsFilled()) {
			AuthorizedUser updatedUserDetails = user;
			updatedUserDetails.setAddtionDetailsFilled(true);
			Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
			Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUserDetails,
					currentAuthentication.getCredentials(), currentAuthentication.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		}

		return updateStatus;
	}

	@PostMapping("/jobAction")
	public String jobAction(final Model model, @AuthenticationPrincipal final AuthorizedUser user,
			@RequestParam final int jobId, @RequestParam final int flag, final HttpSession session) {
		session.setAttribute("jobActionJobId", jobId);
		session.setAttribute("jobActionFlag", flag);
		return "redirect:/employer/act/jobPost";
	}

	@PostMapping("/updateIsViewed/{jobId}")
	@ResponseBody
	public int updateIsViewed(@PathVariable final int jobId) {
		return employerService.updateIsViewed(jobId);
	}

	@PostMapping("/deleteApplicant/{appliedJobId}")
	@ResponseBody
	public int deleteApplicant(@PathVariable final int appliedJobId) {

		return employerService.deleteApplicant(appliedJobId);

	}

	@PostMapping("/shortListApplicant/{appliedJobId}")
	@ResponseBody
	public int shortListApplicant(@PathVariable final int appliedJobId) {

		return employerService.shortListApplicant(appliedJobId);

	}

	@PostMapping("/updateErProfile")
	@ResponseBody
	public int updateProfile(@RequestParam("address1") final String address1,
			@RequestParam("address2") final String address2, @RequestParam("state") final String state,
			@RequestParam("city") final String city, @RequestParam("zipcode") final String zipcode,
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
				newName = "Employer_" + user.getPhone() + "_profile." + extension;
				byte[] bytes = file.getBytes();
				Path path = Paths.get(PHOTO_LOCATION + newName);
				Files.write(path, bytes);
				userDetails.setPhoto_path(path.toString());
			}

			userDetails.setAddress_line1(address1);
			userDetails.setAddress_line2(address2);
			userDetails.setState_id(state);
			userDetails.setCity_id(city);
			userDetails.setZipcode(zipcode);
			userDetails.setFlag("edit_profile");
			userDetails.setUserType("2");
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

	@ResponseBody
	@PostMapping("/postNewJob")
	public int postNewJob(@RequestBody final JobDetails jobDetails,
			@AuthenticationPrincipal final AuthorizedUser myUser) {
		return employerService.updateJobDetails(jobDetails, myUser);
	}

	@ResponseBody
	@PostMapping("/updateFilledPositions")
	public int modifyFilledPositions(@RequestBody final JobFilledData fillData,
			@AuthenticationPrincipal final AuthorizedUser user) {

		return employerService.modifyFilledPositions(fillData, user);
	}
}
