package com.quantum.ampmjobs.controller;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quantum.ampmjobs.dto.Otp;
import com.quantum.ampmjobs.entities.AuthenticationUser;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.service.UserService;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Controller
@RequestMapping({ "/home", "/public" })
public class RegistrationController {

	@Autowired
	private UserService userService;

	@Autowired
	private PublicService publicService;

	@GetMapping("/registration/{flag}")
	public String registration(@PathVariable final String flag, final Model model) {
		model.addAttribute("dbFlag", flag);
		return "common/signup";
	}

	@ResponseBody
	@PostMapping("/generateOTP")
	public int generateOTP(@RequestBody final Otp loginDetails) {
		return userService.createUserAccount(Long.parseLong(loginDetails.getMobile()),
				Integer.parseInt(loginDetails.getFlag()));
	}

	@ResponseBody
	@PostMapping("/checkMobileAndGenerateOTP")
	public int checkMobileAndGenerateOTP(@RequestBody final Otp loginDetails) {
		return userService.verifyUserAndGetOTP(loginDetails);
	}

	@ResponseBody
	@PostMapping("/checkOTP/{userMobileNo}/{userOTPcode}")
	public int validateOTP(@PathVariable final long userMobileNo, final HttpSession session,
			@PathVariable final String userOTPcode) {
		session.setAttribute("userMobile", userMobileNo);
		return userService.validateOTP(userMobileNo, userOTPcode);
	}

	@GetMapping("/generatePassword/{encodedCode}/{encodedMobile}/{flag}")
	public String registration(@PathVariable final String encodedCode, @PathVariable final String encodedMobile,
			@PathVariable final int flag, final Model model) {
		model.addAttribute("dbMobileNo", encodedMobile);

		model.addAttribute("dbFlag", flag);
		model.addAttribute("userAccount", new AuthenticationUser());
		String mobile = new String(Base64.getDecoder().decode(encodedMobile.getBytes()));
		LoginDetails details = publicService.findLoginDetailsByMobile(Long.parseLong(mobile));
		String code = new String(Base64.getDecoder().decode(encodedCode.getBytes()));
		boolean isLinkValid = ActivityUtilities.isLinkValid(code, details.getOtp(), details.getExpireDate());
		model.addAttribute("isLinkValid", isLinkValid);
		model.addAttribute("dbEmail", details.getEmail());
		model.addAttribute("encodedCode", encodedCode);
		return "common/account-creation";
	}

	@ResponseBody
	@PostMapping("/checkPassword/{userMobileNo}/{encodercode}/{pwd}/{cnfPwd}")
	public int checkPassword(@PathVariable final String userMobileNo, @PathVariable final String encodercode,
			@PathVariable final String pwd, @PathVariable final String cnfPwd, final HttpServletRequest request) {
		String mobile = new String(Base64.getDecoder().decode(userMobileNo.getBytes()));
		String code = new String(Base64.getDecoder().decode(encodercode.getBytes()));
		return userService.validatePassword(Long.parseLong(mobile), Integer.valueOf(code), pwd, cnfPwd, request);
	}

	@GetMapping("/resetPassword/{dbflag}")
	public String resetPassword(@PathVariable final String dbflag, final Model model) {
		model.addAttribute("dbflag", dbflag);
		return "common/password-reset-link";
	}

	@ResponseBody
	@PostMapping("/resetPassword/{userMobileNo}/{pwd}/{cnfPwd}")
	public int resetPassword(@PathVariable final String userMobileNo, @PathVariable final String pwd,
			@PathVariable final String cnfPwd) {
		return userService.resetPassword(Long.parseLong(userMobileNo), pwd, cnfPwd, "");
	}

}
