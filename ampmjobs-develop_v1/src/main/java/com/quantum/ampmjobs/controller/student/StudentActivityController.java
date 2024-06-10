package com.quantum.ampmjobs.controller.student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.JobTitles;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.UserDetails;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.service.StudentService;
import com.quantum.ampmjobs.service.UserService;

@Controller
@RequestMapping("/student/act")
public class StudentActivityController {

	@Autowired
	private StudentService studentService;

	@Autowired
	private UserService userService;

	@Autowired
	private PublicService publicService;

	@Value("${lu.db.property.state}")
	private String stateProperty;

	@Value("${lu.db.property.city}")
	private String cityProperty;

	@Value("${profile.photos.upload.dir}")
	private String PHOTO_LOCATION;

	@GetMapping("/dashboard")
	public String studentHome(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {

		model.addAttribute("activeClass", "dashboard");

		model.addAttribute("mappedJobs", studentService.getMappedJobs(user));

		model.addAttribute("statistics", userService.getSiteStatistics());

		return "student/dashboard";
	}

	@GetMapping("/searchJob")
	public String searchJob(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {
		model.addAttribute("activeClass", "jobSearch");
		model.addAttribute("jobsList", studentService.getAllJobsBySearch(user));
		return "student/search-job";
	}

	@GetMapping("/myJobs")
	public String myJobs(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {
		model.addAttribute("activeClass", "myJobs");

		List<JobDetails> jobs = studentService.getAppliedJobs(user);
		model.addAttribute("appliedJobs", jobs);
		return "student/my-jobs";
	}

	@GetMapping("/editProfile")
	public String editProfile(final Model model, @AuthenticationPrincipal final AuthorizedUser myUser) {
		model.addAttribute("activeClass", "editProfile");
		UserDetails userDetails = userService.getProfileDetails(myUser, 1);

		if (userDetails.getCountry_id() != null) {
			List<MyJsonData> stateData = publicService.getCommonData(stateProperty,
					Integer.parseInt(userDetails.getCountry_id()));
			model.addAttribute("stateList", stateData);

			List<MyJsonData> cityData = publicService.getCommonData(cityProperty,
					Integer.parseInt(userDetails.getState_id()));
			model.addAttribute("cityList", cityData);
			userDetails.setStateId(Integer.parseInt(userDetails.getState_id()));

			if (userDetails.getCity_id() != null) {
				List<MyJsonData> locationData = publicService.getCommonData("location",
						Integer.parseInt(userDetails.getCity_id()));
				model.addAttribute("locationData", locationData);
				userDetails.setLocation_id(Integer.parseInt(userDetails.getLocation()));
			}

		}
		List<JobTitles> preferredJobTitles = studentService.getPreferredJobTitles(myUser);
		model.addAttribute("myTitles", preferredJobTitles);
		model.addAttribute("userDetails", userDetails);
		return "student/edit-profile";
	}

}
