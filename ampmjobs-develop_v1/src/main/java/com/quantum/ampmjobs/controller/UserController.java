package com.quantum.ampmjobs.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PublicService publicService;

	@Value("${profile.photos.upload.dir}")
	private String PHOTO_LOCATION;

	@GetMapping("/regCompleted")
	public String regCompleted() {
		return "common/account-completion";
	}

	@GetMapping("/updatePassword")
	public String resetPassword() {
		return "password-change";
	}

	@ResponseBody
	@PostMapping("/changePassword/{oldpwd}/{pwd}/{cnfPwd}")
	public int changePassword(@AuthenticationPrincipal final AuthorizedUser user, @PathVariable final String oldpwd,
			@PathVariable final String pwd, @PathVariable final String cnfPwd) {

		LoginDetails loginDetails = publicService.findLoginDetailsByEmail(user.getUsername());

		return userService.resetPassword(loginDetails.getPhone(), pwd, cnfPwd, oldpwd);
	}

	@GetMapping("/loadTemplate/{uniqueId}/{flag}")
	public String loadTemplate(final Model model, @PathVariable final int uniqueId, @PathVariable final String flag) {
		List<MyJsonData> data = publicService.getCommonData(flag, uniqueId);
		model.addAttribute("luData", data);
		model.addAttribute("flag", flag);

		return "data-template";
	}

	@GetMapping("/viewDp/{picPath}")
	public ResponseEntity<Resource> showImage(@PathVariable final String picPath,
			@AuthenticationPrincipal final AuthorizedUser user) {
		Path imagePath = Paths.get(PHOTO_LOCATION, user.getPhotoPath());

		try {
			Resource resource = new UrlResource(imagePath.toUri());
			if (resource.exists() || resource.isReadable()) {
				return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}

}
