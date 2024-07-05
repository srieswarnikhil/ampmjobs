package com.quantum.ampmjobs.service.impl;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.quantum.ampmjobs.api.payment.utility.ApiResponse;
import com.quantum.ampmjobs.api.payment.utility.PaymentReCheckResponse;
import com.quantum.ampmjobs.dao.DiscountDetailDao;
import com.quantum.ampmjobs.dao.LoginDetailsRepository;
import com.quantum.ampmjobs.dao.PaymentMasterDao;
import com.quantum.ampmjobs.dao.UserRepository;
import com.quantum.ampmjobs.dto.MyJsonData;
import com.quantum.ampmjobs.dto.PaymentOfferDetails;
import com.quantum.ampmjobs.entities.DiscountDetail;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.entities.PaymentMaster;
import com.quantum.ampmjobs.service.PublicService;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Service
public class PublicServiceImpl implements PublicService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private LoginDetailsRepository loginRepo;

	@Autowired
	private PaymentMasterDao paymentMasterDao;

	@Autowired
	private DiscountDetailDao discountDetailDao;

	@Autowired
	@Qualifier("db.props")
	private Properties messageSource;

	@Autowired
	private UserRepository userRepo;

	@Override
	public List<MyJsonData> getCommonData(final String param1, final int param2) {
		String sql = "SELECT public.udfun_get_dropdown(?, ?)";
		String res = jdbcTemplate.queryForObject(sql, String.class, param1, param2);

		return res == null ? new ArrayList<>() : ActivityUtilities.convertJsonIntoObject(res, new ArrayList<>());

	}

	@Override
	public LoginDetails findLoginDetailsByMobile(final long mobile) {
		return loginRepo.findLoginDetailsByMobile(mobile);
	}

	@Override
	public LoginDetails findLoginDetailsByEmail(final String email) {
		return loginRepo.findLoginDetailsByEmail(email);
	}

	@Override
	public void updateLoginDetails(final LoginDetails details) {
		loginRepo.save(details);

	}

	@Override
	public String generateMtrId() {
		String mTrId = ActivityUtilities.generateMerchantTransactionId();
		String message = messageSource.getProperty("check_mr_tr_id_available");
		String sql = MessageFormat.format(message, "'" + mTrId + "'");
		String dbCode = userRepo.getUniqueResult(sql);
		return "0".equals(dbCode) ? mTrId : generateMtrId();

	}

	@Override
	public List<PaymentMaster> getDefaultPayments(final String category) {

		List<PaymentMaster> list = paymentMasterDao.findByPaymentCategoryAndIsActiveTrue(category);

		Currency indianRupee = Currency.getInstance("INR");

		// Get the NumberFormat instance for Indian Rupee currency format
		NumberFormat indianCurrencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

		// Set the currency for the NumberFormat
		indianCurrencyFormat.setCurrency(indianRupee);

		// Format the number as Indian Rupee currency

		list.stream().forEach(kk -> {
			kk.setPaymentAmountText(indianCurrencyFormat.format(kk.getPaymentAmount()));
		});

		return list;

	}

	@Override
	public String getDiscountInPercentage(final long userId, final String payId) {
		String discountAmountMessage = messageSource.getProperty("get_user_discount_percentage");
		String discountSql = MessageFormat.format(discountAmountMessage, "'" + userId + "'", payId);
		return userRepo.getUniqueResult(discountSql);
	}

	@Override
	public String getMasterPayment(final String payId) {
		String masterAmountMessage = messageSource.getProperty("get_user_master_amount");
		String masterSql = MessageFormat.format(masterAmountMessage, payId);
		return userRepo.getUniqueResult(masterSql);
	}

	@Override
	public long calculateDiscount(final long userId, final String payId) {

		// get discount details
		String discountInPercentage = getDiscountInPercentage(userId, payId);

		// get master pay details
		String masterAmount = getMasterPayment(payId);

		return gerateFinalAmountToBePay(discountInPercentage, masterAmount);

	}

	private long gerateFinalAmountToBePay(final String discountInPercentage, final String masterAmount) {
		if (discountInPercentage != null && masterAmount != null) {
			double effectiveAmount = (Double.valueOf(masterAmount))
					* (1 - ((Double.valueOf(discountInPercentage)) / 100.0));

			return Math.round(effectiveAmount);
		}

		return Long.parseLong(masterAmount);

	}

	@Override
	public int insertPaymentAPIResponse(final String jsonInput, final ApiResponse paymentResponse, final String mtrId,
			final String mUrId, final String payId) {

		String sql = "call public.usp_proc_payment_history(?,?,?,?::jsonb,?::jsonb,?)";
		String json = ActivityUtilities.convertObjectIntoJson(paymentResponse);
		String res = jdbcTemplate.queryForObject(sql, String.class, Integer.parseInt(payId), mtrId, mUrId, jsonInput,
				json, "");
		System.out.println("insert first pay response in DB. status : " + res);
		return res == null ? 0 : Integer.parseInt(res);

	}

	@Override
	public String getMrTrId(final long userId) {
		String mrTrIdMessage = messageSource.getProperty("get_mr_tr_id");
		String sql = MessageFormat.format(mrTrIdMessage, "'" + userId + "'");
		return userRepo.getUniqueResult(sql);
	}

	@Override
	public int updateRecheckPaymentResponse(final PaymentReCheckResponse paymentReCheckResponse, final String mtrId,
			final long userId) {

		String sql = "call public.usp_proc_payment_history_update(?,?::jsonb,?)";
		String json = ActivityUtilities.convertObjectIntoJson(paymentReCheckResponse);
		String queryForObject = jdbcTemplate.queryForObject(sql, String.class, mtrId, json, "");
		System.out.println("re-check status: " + queryForObject);

		return queryForObject == null ? 0 : Integer.parseInt(queryForObject);
	}

	@Override
	public PaymentOfferDetails getEffectiveDiscountDetails(final String email, final long phone,
			final PaymentOfferDetails pod) {

		LoginDetails loginDetails = loginRepo.findByEmailAndPhone(email, phone);

		DiscountDetail effectiveDiscountDetails = discountDetailDao.findByUserIdAndIsActiveIsTrue(loginDetails.getId());
		if (effectiveDiscountDetails != null) {
			String discountPercentage = String.valueOf(effectiveDiscountDetails.getDiscountPercentage());

			if (Double.valueOf(discountPercentage) == 0) {
				pod.setDiscountApplicable(false);
			} else {

				Optional<PaymentMaster> masterPay = paymentMasterDao
						.findById(effectiveDiscountDetails.getPaymentLookupId());
				if (masterPay.isPresent()) {
					PaymentMaster master = masterPay.get();

					pod.setDiscountApplicable(true);
					pod.setDiscountPercentage(Double.parseDouble(discountPercentage));
					pod.setDuration(master.getPaymentType());
					pod.setEffectiveAmount(
							gerateFinalAmountToBePay(discountPercentage, String.valueOf(master.getPaymentAmount())));
					pod.setMasterPaymentId(master.getPaymentLookupId());
					pod.setRegFee(master.getPaymentAmount());
				}

			}
		}

		return pod;

	}

	@Override
	public String getDiscountPercentageByUserId(final long userId) {
		String discountAmountMessage = messageSource.getProperty("get_user_discount_by_user_id");
		String discountSql = MessageFormat.format(discountAmountMessage, "'" + userId + "'");
		return userRepo.getUniqueResult(discountSql);
	}

	@Override
	public Date generatePaymentExpireDate(final String email, final long phone) {
		try {
			LocalDate LD = LocalDate.now(ZoneId.systemDefault());

			String message = messageSource.getProperty("get_active_duration_from_payment_history");
			String sql = MessageFormat.format(message, "'" + phone + "'", "'" + email + "'");
			String duration = userRepo.getUniqueResult(sql);

			if ("Monthly".equalsIgnoreCase(duration)) {
				LD = LD.plusMonths(1);
			} else if ("Quarterly".equalsIgnoreCase(duration)) {
				LD = LD.plusMonths(3);
			} else if ("Half Yearly".equalsIgnoreCase(duration)) {
				LD = LD.plusMonths(6);
			} else if ("Yearly".equalsIgnoreCase(duration)) {
				LD = LD.plusYears(1);
			}

			return java.sql.Date.valueOf(LD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}

}
