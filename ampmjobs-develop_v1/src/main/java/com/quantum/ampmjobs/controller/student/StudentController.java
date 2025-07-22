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

		return "student/payment";
	}

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
