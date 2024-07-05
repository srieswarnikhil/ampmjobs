package com.quantum.ampmjobs.service;

import java.util.Date;
import java.util.List;

import com.quantum.ampmjobs.api.payment.utility.ApiResponse;
import com.quantum.ampmjobs.api.payment.utility.PaymentReCheckResponse;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.PaymentOfferDetails;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.entities.PaymentMaster;

public interface PublicService {

	List<MyJsonData> getCommonData(String countryCode, int i);

	LoginDetails findLoginDetailsByMobile(long mobile);

	LoginDetails findLoginDetailsByEmail(String email);

	void updateLoginDetails(LoginDetails details);

	String generateMtrId();

	List<PaymentMaster> getDefaultPayments(String string);

	long calculateDiscount(long userId, String payId);

	int insertPaymentAPIResponse(String jsonInput, ApiResponse paymentResponse, String mtrId, String mUrId,
			String payId);

	String getMrTrId(long userId);

	int updateRecheckPaymentResponse(PaymentReCheckResponse paymentReCheckResponse, String mtrId, long userId);

	String getDiscountInPercentage(long userId, String payId);

	String getMasterPayment(String payId);

	PaymentOfferDetails getEffectiveDiscountDetails(String email, long phone, PaymentOfferDetails pod);

	String getDiscountPercentageByUserId(long userId);

	Date generatePaymentExpireDate(String username, long phone);

}
