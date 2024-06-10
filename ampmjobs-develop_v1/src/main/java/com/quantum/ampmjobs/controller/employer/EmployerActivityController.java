package com.quantum.ampmjobs.controller.employer;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quantum.ampmjobs.dto.Applicants;
import com.quantum.ampmjobs.dto.JobDetails;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.UserDetails;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.service.EmployerService;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.service.UserService;

@Controller
@RequestMapping("/employer/act")
public class EmployerActivityController {

	@Value("${lu.db.property.state}")
	private String stateProperty;

	@Value("${lu.db.property.city}")
	private String cityProperty;

	@Value("${profile.photos.upload.dir}")
	private String PHOTO_LOCATION;

	@Autowired
	private PublicService publicService;

	@Autowired
	private UserService userService;

	@Autowired
	private EmployerService employerService;

	@GetMapping("/dashboard")
	public String employerHome(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {

		model.addAttribute("activeClass", "dashboard");
		model.addAttribute("jobTitles", employerService.getPreferredJobTitles(user));
		model.addAttribute("statistics", userService.getSiteStatistics());

		return "employer/dashboard";
	}

	@GetMapping("/jobPost")
	public String posJob(final Model model, @AuthenticationPrincipal final AuthorizedUser user,
			final HttpSession session) {
		model.addAttribute("activeClass", "jobPost");
		// load cities based on state id
		String query = "select city_id from employer where email = '" + user.getUsername() + "' limit 1";
		String res = userService.getUniqueResult(query);

		List<MyJsonData> data = publicService.getCommonData("location", res != null ? Integer.parseInt(res) : 0);
		model.addAttribute("locationData", data);

		List<MyJsonData> jobNames = employerService.getJobTitles(user.getUsername(), user.getPhone());
		model.addAttribute("jobNames", jobNames);
		Object jobId = session.getAttribute("jobActionJobId");
		Object flag = session.getAttribute("jobActionFlag");
		if (jobId != null && flag != null) {
			int jobActionJobId = (int) jobId;
			int jobActionFlag = (int) flag;

			model.addAttribute("jobDetails", employerService.getJobDetailsById(jobActionJobId));
			model.addAttribute("jobFlag", jobActionFlag);
			session.removeAttribute("jobActionJobId");
			session.removeAttribute("jobActionFlag");
		} else {
			model.addAttribute("jobDetails", new JobDetails());
		}
		return "employer/post-job";
	}

	@GetMapping("/postedJobs")
	public String postedJobs(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {
		model.addAttribute("activeClass", "postedJobs");
		model.addAttribute("postedJobs", employerService.getPostedJobs(user));
		return "employer/posted-jobs";
	}

	@GetMapping("/applicants")
	public String applicants(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {
		model.addAttribute("activeClass", "applicants");
		List<Applicants> applicants = employerService.getApplicants(user);
		model.addAttribute("applicants", applicants);
		return "employer/applicants";
	}

	@GetMapping("/editProfile")
	public String editProfile(final Model model, @AuthenticationPrincipal final AuthorizedUser user) {
		model.addAttribute("activeClass", "editProfile");
		UserDetails userDetails = userService.getProfileDetails(user, 2);
		userDetails.setStateId(Integer.parseInt(userDetails.getState_id()));
		model.addAttribute("userDetails", userDetails);

		List<MyJsonData> stateData = publicService.getCommonData(stateProperty, 77);
		model.addAttribute("stateList", stateData);

		List<MyJsonData> cityData = publicService.getCommonData(cityProperty,
				Integer.parseInt(userDetails.getState_id()));
		model.addAttribute("cityList", cityData);

		return "employer/edit-profile";
	}

}
