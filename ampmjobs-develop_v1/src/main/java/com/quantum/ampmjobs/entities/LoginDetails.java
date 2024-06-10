package com.quantum.ampmjobs.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "login_detail")
@Data
@ToString
public class LoginDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "login_id")
	private long id;

	@Column(name = "phone")
	private long phone;

	@Column(name = "email")
	private String email;

	@Column(name = "otp")
	private String otp;

	@Column(name = "pwd")
	private String password;

	@Column(name = "roletype")
	private String role;

	@Column(name = "is_otp_verified")
	private boolean isMobileVerified;

	@Column(name = "is_email_verified")
	private boolean isEmailVerified;

	@Column(name = "is_payment_verified")
	private boolean isPaymentVerified;

	@Column(name = "user_ip_address")
	private String ipAddress;

	@Column(name = "link_expire_time")
	private Date expireDate;

	@Column(name = "payment_expire_date")
	private Date paymentExpireDate;

}
