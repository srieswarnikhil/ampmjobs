package com.quantum.ampmjobs.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.quantum.ampmjobs.dao.DiscountDetailDao;
import com.quantum.ampmjobs.dao.LoginDetailsRepository;
import com.quantum.ampmjobs.dto.CustomerDetails;
import com.quantum.ampmjobs.entities.DiscountDetail;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private LoginDetailsRepository ldr;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DiscountDetailDao ddd;

	@Override
	public CustomerDetails fetchUserDetails(final String email, final String phone) {
		CustomerDetails cd = new CustomerDetails();
		LoginDetails ld = ldr.findByEmailAndPhone(email, Long.parseLong(phone));

		if (ld != null) {
			cd = new CustomerDetails(ld.getEmail(), ld.getPhone(), ld.getRole(), 0, ld.getId());

		}
		return cd;
	}

	@Override
	public int updateUserDiscount(final String discount, final String userId, final String masterPayId) {

		int response = 0;
		try {

			String sql = "call public.usp_proc_payment_discount_detail(" + Double.valueOf(discount) + ","
					+ Long.valueOf(userId) + "," + Integer.valueOf(masterPayId) + ",'0')";

			String res = jdbcTemplate.queryForObject(sql, String.class);
			response = res == null ? 0 : Integer.parseInt(res);

		} catch (Exception e) {
			response = 0;
			e.printStackTrace();
		}
		return response;

	}

	@Override
	public String getDidcountDetails(final String email, final String phone) {

		LoginDetails loginDetails = ldr.findByEmailAndPhone(email, Long.parseLong(phone));
		if (loginDetails != null) {
			DiscountDetail dd = ddd.findByUserIdAndIsActiveIsTrue(loginDetails.getId());

			if (dd != null) {
				return String.valueOf(dd.getDiscountPercentage());
			}
		}

		return "";
	}

	@Override
	public DiscountDetail getDidcountDetailsByUserId(final long userId) {
		DiscountDetail discountDetail = ddd.findByUserIdAndIsActiveIsTrue(userId);
		return discountDetail;
	}

	@Override
	public List<String> getUserDetails(final String inData) {

		Pageable pageable = PageRequest.of(0, 5);

		Optional<List<LoginDetails>> optionalList = Optional
				.ofNullable(ldr.findByPhoneOrEmailContainingIgnoreCase(inData, pageable));

		return optionalList.map(
				list -> list.stream().map(kk -> kk.getEmail() + " - " + kk.getPhone()).collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

}
