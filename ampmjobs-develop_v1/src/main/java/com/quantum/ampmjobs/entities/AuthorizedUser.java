package com.quantum.ampmjobs.entities;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorizedUser extends User {

	private static final long serialVersionUID = -1741175373309274363L;

	public AuthorizedUser(final String username, final String password, final boolean enabled,
			final boolean accountNonExpired, final boolean credentialsNonExpired, final boolean accountNonLocked,
			final Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	private String name;

	private long phone;

	private long userId;

	private String role;

	private String photoPath;

	private boolean isPaymentCompleted;

	private boolean isAddtionDetailsFilled;

	private String paymentExpireDate;

}