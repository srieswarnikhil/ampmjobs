package com.quantum.ampmjobs.api.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SMSUtility {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${sms.key}")
	private String key;

	@Async
	public void sentRegistrationOtp(final long mobileNo, final String otp) {
		String sms = "Dear Subscriber\\n\\n";
		sms += "Your OTP for verifying your account with ampmjobs is: " + otp + " \\n\\n";
		sms += "Please enter this code to complete the verification process, within 5 minutes\\n\\n";
		sms += "Please do not share this OTP with anyone ---- support@ampmjobs.in \\n\\n";
		sms += "QUITES";

		sentOtpFromAPI(mobileNo, sms.toString());

	}

	@Async
	public void sendSMStoShortlistedStudent(final String companyName, final long studentMobile) {
		String sms = "Dear Applicant\\n\\n";
		sms += "You have been shortlisted by " + companyName + " Employer please login to ampmjobs.in ";
		sms += "dashboard to know the details.\\n\\n";
		sms += "All the best ----- support@ampmjobs.in\\n\\n";
		sms += "QUITES";

		sentOtpFromAPI(studentMobile, sms.toString());
	}

	public void sentOtpFromAPI(final long mobileNo, final String smsBody) {
		String jsonInput = "{\"filetype\": \"2\"," + "\"msisdn\": [\"" + mobileNo + "\"]," + "\"language\": \"0\","
				+ "\"credittype\": \"7\"," + "\"senderid\": \"Quites\"," + "\"templateid\": \"0\"," + "\"message\": \""
				+ smsBody + "\"," + "\"ukey\": \"" + key + "\"}";

		String url = "https://api.voicensms.in/SMSAPI/webresources/CreateSMSCampaignPost";

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> requestEntity = new HttpEntity<>(jsonInput, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				System.out.println("SMS sent successfully");
			} else {
				System.out.println("SMS failed to sent");
			}
		} catch (Exception e) {
			System.out.println("SMS failed to sent");
			e.printStackTrace();
		}
	}

}
