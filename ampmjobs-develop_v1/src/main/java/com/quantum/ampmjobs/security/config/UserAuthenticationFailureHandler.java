
package com.quantum.ampmjobs.security.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class UserAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException exception) throws IOException, ServletException {
		String contextPath = request.getContextPath();
		if (exception.getMessage().contains("User account has expired")) {
			response.sendRedirect(contextPath + "/login?error=1");
		} else if (exception.getMessage().contains("Bad credentials")) {
			response.sendRedirect(contextPath + "/login?error=true");
		} else if (exception.getMessage().contains("User credentials have expired")) {
			response.sendRedirect(contextPath + "/login?error=2");
		} else if (exception.getMessage().contains("User account is locked")) {
			response.sendRedirect(contextPath + "/login?error=4");
		} else {
			response.sendRedirect(contextPath + "/login?error=true");
		}
	}
}
