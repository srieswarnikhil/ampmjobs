package com.quantum.ampmjobs.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quantum.ampmjobs.entities.AuthorizedUser;

@Controller
@RequestMapping("/user")
public class DashboardController {

	@GetMapping("/home")
	public String dashboard(@AuthenticationPrincipal final AuthorizedUser user) {

		if ("STUDENT".equals(user.getRole())) {
			return "redirect:/student/act/dashboard";
		} else if ("EMPLOYER".equals(user.getRole())) {
			return "redirect:/employer/act/dashboard";
		} else if ("ADMIN".equals(user.getRole())) {
			return "redirect:/admin/dashboard";
		} else {
			return "redirect:/login";
		}

	}
}
