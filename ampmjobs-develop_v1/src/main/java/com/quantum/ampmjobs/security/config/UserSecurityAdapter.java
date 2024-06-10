
package com.quantum.ampmjobs.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class UserSecurityAdapter extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

				.authorizeRequests(requests -> requests.antMatchers("/student/**").hasRole("STUDENT")
						.antMatchers("/admin/**").hasRole("ADMIN").antMatchers("/employer/**").hasRole("EMPLOYER")
						.antMatchers("/user/**").hasAnyRole("STUDENT", "EMPLOYER", "ADMIN").anyRequest().permitAll())
				.formLogin(login -> login.loginPage("/login").failureHandler(new UserAuthenticationFailureHandler())
						.defaultSuccessUrl("/user/home", true).permitAll())
				.exceptionHandling(excep -> excep.accessDeniedPage("/userAccessDenied"))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?error=logout")
						.deleteCookies("JSESSIONID").permitAll());

		http.sessionManagement(management -> {
			management.maximumSessions(1).expiredUrl("/login?error=du");
			management.invalidSessionUrl("/login?error=sot");
		});

	}

	@Override
	public void configure(final WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/assets/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	protected PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
