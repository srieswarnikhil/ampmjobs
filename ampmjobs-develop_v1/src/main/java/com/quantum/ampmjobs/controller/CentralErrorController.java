package com.quantum.ampmjobs.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CentralErrorController implements ErrorController {

	@GetMapping("/error")
	public String handleError() {
		return "error/error-page";
	}
}