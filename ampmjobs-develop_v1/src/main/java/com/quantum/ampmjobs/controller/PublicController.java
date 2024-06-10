package com.quantum.ampmjobs.controller;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;

import com.quantum.ampmjobs.api.utility.EmailUtility;
import com.quantum.ampmjobs.dto.UserDetails;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.service.UserService;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Controller
@RequestMapping({ "/", "/home" })
public class PublicController {
	@Autowired
	private PublicService publicService;

	@Value("${lu.db.property.country}")
	private String countryKey;

	@Value("${applicationUrl}")
	private String applicationUrl;

	@Autowired
	private EmailUtility emailUtility;

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String home(final Model model) {
		model.addAttribute("activeClass", "home");
		return "index";
	}

	@GetMapping("/basicDetails/{flag}")
	public String registration(final Model model, @PathVariable final String flag, final HttpSession session) {
		String mobile = String.valueOf(session.getAttribute("userMobile"));
		String encodedMobile = new String(Base64.getEncoder().encode(mobile.getBytes()));

		model.addAttribute("dbMobile", encodedMobile);
		model.addAttribute("userMobile", mobile);
		model.addAttribute("flag", flag);

		LoginDetails loginDetails = publicService.findLoginDetailsByMobile(Long.parseLong(mobile));
		String userEmail = "";
		if (loginDetails.isEmailVerified()) {
			userEmail = loginDetails.getEmail();
		} else {
			model.addAttribute("countryData", publicService.getCommonData(countryKey, 0));
		}
		model.addAttribute("userEmail", userEmail);

		if ("1".equals(flag)) {
			return "student/basic-info";
		} else if ("2".equals(flag)) {
			return "employer/basic-info";
		} else {
			return "/";
		}
	}

	@PostMapping("/updateBasicDetails")
	@ResponseBody
	public int submitForm(@RequestBody final UserDetails userDetails) {
		int res = userService.isEmailExist(userDetails.getEmail());
		if (res < 1) {
			try {
				String encodedMobile = userDetails.getPhone();
				String mobile = new String(Base64.getDecoder().decode(encodedMobile.getBytes()));
				userDetails.setPhone(mobile);
				userDetails.setUserType(userDetails.getFlag());
				userDetails.setFlag("basic_details");
				res = userService.updateUserDetails(userDetails);
				if (res == 1) {
					Context context = new Context();
					String code = ActivityUtilities.generateOTP();

					String enCode = new String(Base64.getEncoder().encode(String.valueOf(code).getBytes()));
					context.setVariable("linkInfo", applicationUrl + "/public/generatePassword/" + enCode + "/"
							+ encodedMobile + "/" + userDetails.getUserType());
					emailUtility.emailWithTemplate(userDetails.getEmail(), "Verify Your Account with ampmjobs.in",
							"email-template/password-creation", context);

					userService.updateOTP(code, userDetails.getEmail());
				}
			} catch (Exception e) {
				res = 0;
				e.printStackTrace();
			}
		}
		return res;
	}

	@GetMapping("/aboutUs")
	public String aboutUs(final Model model) {
		model.addAttribute("activeClass", "aboutUs");
		model.addAttribute("headTitle", "About Us");
		return "about-us";
	}

	@GetMapping("/aboutStudents")
	public String aboutStudents(final Model model) {
		model.addAttribute("activeClass", "aboutStudents");
		model.addAttribute("headTitle", "Students");
		return "students";
	}

	@GetMapping("/aboutEmployers")
	public String aboutEmployers(final Model model) {
		model.addAttribute("activeClass", "aboutEmployers");
		model.addAttribute("headTitle", "Employers");
		return "employers";
	}

	@GetMapping("/terms-conditions")
	public String termsAndConditions() {
		return "terms-conditions";
	}

	@GetMapping("/contactUs")
	public String contactUs(final Model model) {
		model.addAttribute("activeClass", "contactUs");
		model.addAttribute("headTitle", "Contact Us");

		return "contact-us";
	}

	@ResponseBody
	@PostMapping("/sentResetEmailLink/{email}")
	public int sentResetEmailLink(final Model model, @PathVariable final String email) {
		int res = 2;
		try {
			res = userService.isEmailExist(email);
			if (res > 0) {
				// prepare email link for password reset

				Context context = new Context();
				String encodedEmail = new String(Base64.getEncoder().encode(email.getBytes()));
				String code = ActivityUtilities.generateOTP();
				String enCode = new String(Base64.getEncoder().encode(code.getBytes()));
				context.setVariable("linkInfo",
						applicationUrl + "/home/reGeneratePassword/" + enCode + "/" + encodedEmail);
				emailUtility.emailWithTemplate(email, "Password Reset with ampmjobs.in",
						"email-template/password-reset", context);

				userService.updateOTP(code, email);
				res = 1;
			}
		} catch (Exception e) {
			res = 2;
			e.printStackTrace();
		}
		return res;
	}

	@GetMapping("/reGeneratePassword/{encodedCode}/{encodedEmail}")
	public String reGeneratePassword(@PathVariable final String encodedCode, @PathVariable final String encodedEmail,
			final Model model) {
		try {
			String email = new String(Base64.getDecoder().decode(encodedEmail.getBytes()));
			int emailExist = userService.isEmailExist(email);
			model.addAttribute("encodedEmail", emailExist > 0 ? encodedEmail : "");
			LoginDetails details = publicService.findLoginDetailsByEmail(email);
			boolean isLinkValid = false;
			if (details != null) {
				String code = new String(Base64.getDecoder().decode(encodedCode.getBytes()));
				isLinkValid = ActivityUtilities.isLinkValid(code, details.getOtp(), details.getExpireDate());

			}
			model.addAttribute("isLinkValid", isLinkValid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "common/password-re-generate";
	}

	@ResponseBody
	@PostMapping("/updateResetPassword/{encodedEmail}/{pwd}/{cnfPwd}")
	public int updateResetPassword(@PathVariable final String encodedEmail, @PathVariable final String pwd,
			@PathVariable final String cnfPwd) {
		int res = 0;
		try {
			String email = new String(Base64.getDecoder().decode(encodedEmail.getBytes()));
			return userService.resetPasswordByEmail(email, pwd, cnfPwd);
		} catch (Exception e) {
			res = 0;
			e.printStackTrace();
		}
		return res;
	}

	@GetMapping("/ip")
	@ResponseBody
	public String getIp(final HttpServletRequest request) {
		return System.getProperty("user.name") + ", " + ActivityUtilities.getIPAddress(request);
	}

	@GetMapping("/construction")
	public String construction() {
		return "construction";
	}
}
