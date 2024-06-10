package com.quantum.ampmjobs.api.payment.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantum.ampmjobs.dao.LoginDetailsRepository;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.service.PublicService;

@Service
public class PaymentApi {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${payment.merchantId}")
	private String merchantId;

	@Value("${student.payment.redirectUrl}")
	private String redirectUrl;

	@Value("${student.payment.callbackUrl}")
	private String callbackUrl;

	@Value("${employer.payment.redirectUrl}")
	private String redirectEmployerUrl;

	@Value("${employer.payment.callbackUrl}")
	private String callbackEmployerUrl;

	@Value("${payment.uri}")
	private String uri;

	@Value("${payment.salt.key}")
	private String saltKey;

	@Value("${payment.salt.index}")
	private String saltIndex;

	@Value("${payment.url}")
	private String paymentUrl;

	@Value("${recheck.payment.url}")
	private String reCheckPaymentUrl;

	@Autowired
	private PublicService publicService;

	@Autowired
	private LoginDetailsRepository loginDetailDao;

	public ApiResponse payByStudent(final DiscountDetails discountDetails, final String payId)
			throws NoSuchAlgorithmException {

		ApiResponse paymentResponse = new ApiResponse();
		paymentResponse.setSuccess(false);
		try {

			String mtrId = discountDetails.getMerchantTransactionId();

			LoginDetails loginDetails = loginDetailDao.findByEmailAndPhone(discountDetails.getEmail(),
					discountDetails.getPhone());

			long amountToBePaid = publicService.calculateDiscount(loginDetails.getId(), payId);
			amountToBePaid = amountToBePaid * 100; // amount in Paise.

			String mUrId = "" + loginDetails.getId();
			String uPhNo = discountDetails.getMerchantMobileNo();

			String jsonInput = "{\"merchantId\": \"" + merchantId + "\"," + "\"merchantTransactionId\": \"" + mtrId
					+ "\"," + "\"merchantUserId\": \"" + mUrId + "\"," + "\"amount\": " + amountToBePaid + ","
					+ "\"redirectUrl\": \"" + redirectUrl + "\"," + "\"redirectMode\": \"REDIRECT\","
					+ "\"callbackUrl\": \"" + callbackUrl + "\"," + "\"mobileNumber\": \"" + uPhNo + "\","
					+ "\"paymentInstrument\": {\"type\": \"PAY_PAGE\"}" + "}";

			String encode = Base64.getEncoder().encodeToString(jsonInput.getBytes());

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			String finalHeader = encode + uri + saltKey;
			byte[] hashBytes = digest.digest(finalHeader.getBytes());
			String inData = new String(Hex.encode(hashBytes));

			String fHeader = inData + "###" + saltIndex;

			String jIn = "{\"request\":\"" + encode + "\"}";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("X-VERIFY", fHeader);

			HttpEntity<String> requestEntity = new HttpEntity<>(jIn, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(paymentUrl, HttpMethod.POST, requestEntity,
					String.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				String jsonResponse = responseEntity.getBody();
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				paymentResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);

				int dbRes = publicService.insertPaymentAPIResponse(jsonInput, paymentResponse, mtrId, mUrId, payId);
				if (dbRes == 0) {
					paymentResponse.setSuccess(false);
				}

			} else {
				System.out.println("payment check failed");
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				System.out.println("Resource not found.");
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				System.out.println("Unauthorized access.");
			} else {
				System.out.println("Client error: " + e.getStatusCode());
			}
		} catch (Exception e) {
			System.out.println("payment check failed ");
			e.printStackTrace();
		}
		return paymentResponse;
	}

	public PaymentReCheckResponse checkPaymentStatus(final String email, final long mobile)
			throws NoSuchAlgorithmException {

		PaymentReCheckResponse paymentReCheckResponse = new PaymentReCheckResponse();
		paymentReCheckResponse.setSuccess(false);
		try {
			LoginDetails loginDetails = loginDetailDao.findByEmailAndPhone(email, mobile);
			String mtrId = publicService.getMrTrId(loginDetails.getId());

			if (mtrId != "0") {

				MessageDigest digest = MessageDigest.getInstance("SHA-256");

				String finalHeader = "/pg/v1/status/" + merchantId + "/" + mtrId + "" + saltKey;
				byte[] hashBytes = digest.digest(finalHeader.getBytes());
				String inData = new String(Hex.encode(hashBytes));

				String fHeader = inData + "###" + saltIndex;

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.add("X-VERIFY", fHeader);
				headers.add("X-MERCHANT-ID", merchantId);

				HttpEntity<String> requestEntity = new HttpEntity<>(headers);

				String url = reCheckPaymentUrl + merchantId + "/" + mtrId;

				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
						String.class);
				if (responseEntity.getStatusCode().is2xxSuccessful()) {

					String jsonResponse = responseEntity.getBody();
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					paymentReCheckResponse = objectMapper.readValue(jsonResponse, PaymentReCheckResponse.class);
					int recheckPaymentResponse = publicService.updateRecheckPaymentResponse(paymentReCheckResponse,
							mtrId, loginDetails.getId());
					if (recheckPaymentResponse == 0) {
						paymentReCheckResponse.setSuccess(false);
					}
					System.out.println("payment recheck successfully");
				} else {
					System.out.println("payment recheck  failed ");
				}
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				System.out.println("Resource not found.");
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				System.out.println("Unauthorized access.");
			} else {
				System.out.println("Client error: " + e.getStatusCode());
			}
		} catch (Exception e) {
			System.out.println("payment recheck  failed");
			e.printStackTrace();
		}

		return paymentReCheckResponse;

	}

	public ApiResponse payFromEmployer(final DiscountDetails discountDetails, final String payId) {

		ApiResponse paymentResponse = new ApiResponse();
		paymentResponse.setSuccess(false);
		try {

			String mtrId = discountDetails.getMerchantTransactionId();

			LoginDetails loginDetails = loginDetailDao.findByEmailAndPhone(discountDetails.getEmail(),
					discountDetails.getPhone());

			long amountToBePaid = publicService.calculateDiscount(loginDetails.getId(), payId);
			amountToBePaid = amountToBePaid * 100; // amount in Paise.

			String mUrId = "" + loginDetails.getId();
			String uPhNo = discountDetails.getMerchantMobileNo();

			String jsonInput = "{\"merchantId\": \"" + merchantId + "\"," + "\"merchantTransactionId\": \"" + mtrId
					+ "\"," + "\"merchantUserId\": \"" + mUrId + "\"," + "\"amount\": " + amountToBePaid + ","
					+ "\"redirectUrl\": \"" + redirectEmployerUrl + "\"," + "\"redirectMode\": \"REDIRECT\","
					+ "\"callbackUrl\": \"" + callbackEmployerUrl + "\"," + "\"mobileNumber\": \"" + uPhNo + "\","
					+ "\"paymentInstrument\": {\"type\": \"PAY_PAGE\"}" + "}";

			String encode = Base64.getEncoder().encodeToString(jsonInput.getBytes());

			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			String finalHeader = encode + uri + saltKey;
			byte[] hashBytes = digest.digest(finalHeader.getBytes());
			String inData = new String(Hex.encode(hashBytes));

			String fHeader = inData + "###" + saltIndex;

			String jIn = "{\"request\":\"" + encode + "\"}";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("X-VERIFY", fHeader);

			HttpEntity<String> requestEntity = new HttpEntity<>(jIn, headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(paymentUrl, HttpMethod.POST, requestEntity,
					String.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				String jsonResponse = responseEntity.getBody();
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				paymentResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);

				int dbRes = publicService.insertPaymentAPIResponse(jsonInput, paymentResponse, mtrId, mUrId, payId);
				if (dbRes == 0) {
					paymentResponse.setSuccess(false);
				}
				System.out.println("employer payment check  successfully");
			} else {
				System.out.println("employer payment check failed");
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				System.out.println("Resource not found.");
			} else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				System.out.println("employer Unauthorized access.");
			} else {
				System.out.println("employer Client error: " + e.getStatusCode());
			}
		} catch (Exception e) {
			System.out.println("employer payment check failed ");
			e.printStackTrace();
		}
		return paymentResponse;

	}

}
