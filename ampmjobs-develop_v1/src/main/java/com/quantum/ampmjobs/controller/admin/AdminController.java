package com.quantum.ampmjobs.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quantum.ampmjobs.dto.CustomerDetails;
import com.quantum.ampmjobs.entities.PaymentMaster;
import com.quantum.ampmjobs.service.AdminService;
import com.quantum.ampmjobs.service.PublicService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private PublicService publicService;

	@GetMapping("/dashboard")
	public String dashboard() {
		return "admin/dashboard";
	}

	@GetMapping("/checkDiscount")
	public String checkDiscount(final Model model) {
		model.addAttribute("activeClass", "discounts");
		return "admin/discount";
	}

	@GetMapping("/getUserDetails/{email}/{phone}")
	public String loadTemplate(final Model model, @PathVariable final String email, @PathVariable final String phone) {
		CustomerDetails cd = adminService.fetchUserDetails(email, phone);
		model.addAttribute("cd", cd);
		List<PaymentMaster> pmData = new ArrayList<>();
		String discountPercentage = adminService.getDidcountDetails(email, phone);
		model.addAttribute("currentDiscount", discountPercentage);
		if (!"STUDENT".equals(cd.getRole())) {
			pmData = publicService.getDefaultPayments("Employer");
		}
		model.addAttribute("paymentMasterData", pmData);
		return "admin/customer-info";
	}

	@ResponseBody
	@GetMapping("/updateCdDiscount/{discount}/{userId}/{masterPayId}")
	public int updateCdDiscount(final Model model, @PathVariable final String discount,
			@PathVariable final String userId, @PathVariable final String masterPayId) {

		return adminService.updateUserDiscount(discount, userId, masterPayId);
	}

	@GetMapping("/checkRecruiter")
	public String checkRecruiter(final Model model) {
		model.addAttribute("activeClass", "recruiters");
		return "admin/recruiter";
	}

	@GetMapping("/updatePassword")
	public String updatePassword(final Model model) {
		model.addAttribute("activeClass", "pwdChange");
		return "admin/pwd-change";
	}

	@GetMapping("/getEmailAndPhone")
	@ResponseBody
	public List<String> search(@RequestParam final String inData) {
		return adminService.getUserDetails(inData);
	}

}
