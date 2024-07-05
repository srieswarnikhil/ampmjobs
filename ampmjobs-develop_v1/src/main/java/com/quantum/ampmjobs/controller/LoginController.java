package com.quantum.ampmjobs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quantum.ampmjobs.entities.AuthenticationUser;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String slogin(final ModelMap model, @RequestParam(defaultValue = "") final String error,
			@RequestParam(defaultValue = "1") final String flag) {
		model.addAttribute("userAccount", new AuthenticationUser());
		model.addAttribute("error", error);
		model.addAttribute("flag", flag);
		return "common/login";
	}

}
