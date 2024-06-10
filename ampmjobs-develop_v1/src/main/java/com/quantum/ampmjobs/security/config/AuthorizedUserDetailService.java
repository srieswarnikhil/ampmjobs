
package com.quantum.ampmjobs.security.config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quantum.ampmjobs.dao.LoginDetailsRepository;
import com.quantum.ampmjobs.dao.UserRepository;
import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.entities.LoginDetails;
import com.quantum.ampmjobs.utility.ActivityUtilities;

@Service
@Transactional
public class AuthorizedUserDetailService implements UserDetailsService {

	@Autowired
	private LoginDetailsRepository loginRepository;

	@Autowired
	private UserRepository userRepository;

	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

		LoginDetails user = loginRepository.findLoginDetailsByEmail(username);
		if (user == null || user.getPassword() == null) {
			throw new UsernameNotFoundException("No user found with username: " + username);
		}
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

		boolean enabled = true;
		boolean accountNonExpired = user.isMobileVerified();
		boolean credentialsNonExpired = user.isEmailVerified();
		boolean accountNonLocked = ActivityUtilities.isPaymentNotExpired(user.getPaymentExpireDate());

		AuthorizedUser cUser = new AuthorizedUser(user.getEmail(), user.getPassword(), enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, authorities);
		cUser.setName("");
		cUser.setRole(user.getRole());
		try {
			List<String> mainRoles = Arrays.asList("STUDENT", "EMPLOYER");
			if (enabled && accountNonExpired && credentialsNonExpired
					&& accountNonLocked & mainRoles.contains(user.getRole())) {
				String tableName = "student".equalsIgnoreCase(user.getRole()) ? "student" : "employer";
				Object[] result = userRepository.getMultiResult(tableName, username, user.getPhone());
				cUser.setName((String) result[1]);
				if (result[2] != null) {
					File f = new File((String) result[2]);
					cUser.setPhotoPath(f.getName());
				}
				cUser.setUserId(Long.parseLong((String) result[0]));
				cUser.setRole(user.getRole());
				cUser.setPhone(user.getPhone());
				cUser.setPaymentCompleted(user.isPaymentVerified());

				String query = "select city_id from public." + tableName + " where email = '" + username
						+ "' and phone =" + user.getPhone() + " limit 1";
				String dbRes = userRepository.getUniqueResult(query);
				cUser.setAddtionDetailsFilled(false);
				if (dbRes != null && Integer.valueOf(dbRes) > 0) {
					cUser.setAddtionDetailsFilled(true);
				}

				if (user.getPaymentExpireDate() != null) {
					cUser.setPaymentExpireDate(dateFormatter.format(user.getPaymentExpireDate()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cUser;
	}

}
