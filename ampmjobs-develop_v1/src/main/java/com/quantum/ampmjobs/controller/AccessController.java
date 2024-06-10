package com.quantum.ampmjobs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccessController {

	@GetMapping("/userAccessDenied")
	public String accessDenied() {
		return "user-access-denied";
	}

	@PostMapping("/userAccessDenied")
	public String accessDenied2() {
		return "user-access-denied";
	}
}