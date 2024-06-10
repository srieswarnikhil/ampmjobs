package com.quantum.ampmjobs.utility;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ActivityUtilities {

	static final String OTP_CHARS = "0123456789"; // do not change this
	static final int OTP_LENGTH = 4; // do not change this
	static final String UNIQUE_CODE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // do not change this
	static final int UNIQUE_CODE_LENGTH = 6; // do not change this
	static final int TR_CODE_LENGTH = 20; // do not change this

	static final int expiration_limit = 5; // in min

	public static String generateOTP() {
		return generateCode(OTP_LENGTH, OTP_CHARS);
	}

	public static String generateUniqueJobId() {
		return "AMPM-" + generateCode(UNIQUE_CODE_LENGTH, UNIQUE_CODE_CHARS);
	}

	public static String generateCode(final int coleLength, final String codeInChar) {
		Random random = new Random();
		StringBuilder otp = new StringBuilder(coleLength);

		IntStream.range(0, coleLength).forEach(kk -> {
			otp.append(codeInChar.charAt(random.nextInt(codeInChar.length())));
		});

		return otp.toString().length() == coleLength ? otp.toString() : generateCode(coleLength, codeInChar);
	}

	public static <T> List<T> convertJsonIntoObject(final String input, List<T> output) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			output = mapper.readValue(input, new TypeReference<List<T>>() {
			});
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static <T> String convertObjectIntoJson(final T t) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(t);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isEqual(final String code1, final String code2) {

		if (code1 != null && code2 != null) {
			String in1 = String.valueOf(code1);
			String in2 = String.valueOf(code2);
			return in1.equals(in2);
		}

		return false;
	}

	public static String getIPAddress(final HttpServletRequest request) {

		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_X_FORWARDED");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_FORWARDED");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}

		return ipAddress;
	}

	public static String getComponentValue(final String in, final String encKey) {
		StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
		decryptor.setPassword(encKey);
		return decryptor.decrypt(in);
	}

	public static boolean isLinkValid(final String code, final String otp, final Date expireDate) {

		boolean isSame = isEqual(code, otp);
		return isSame ? isDateValid(expireDate) ? true : false : false;
	}

	public static boolean isDateValid(final Date expireDate) {
		long actualLimit = (Math.abs(new Date().getTime() - expireDate.getTime()) / (1000 * 60));
		return actualLimit - expiration_limit > 0 ? false : true;
	}

	public static int getCurrentYear() {
		Year currentYear = Year.now();
		int yearValue = currentYear.getValue();
		return yearValue;
	}

	public static String generateMerchantTransactionId() {
		return "AMPM-" + getCurrentYear() + "-" + generateCode(TR_CODE_LENGTH, UNIQUE_CODE_CHARS);
	}

	public static boolean isPaymentNotExpired(final Date actualPaymentExpireDate) {
		if (actualPaymentExpireDate != null) {
			// if current date < actualPaymentExpireDate then true else false
			LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
			LocalDate expireDate = actualPaymentExpireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			return currentDate.isBefore(expireDate);
		}
		return true;

	}

}
