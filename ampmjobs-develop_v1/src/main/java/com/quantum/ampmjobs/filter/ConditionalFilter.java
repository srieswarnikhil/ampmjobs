package com.quantum.ampmjobs.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.quantum.ampmjobs.entities.AuthorizedUser;
import com.quantum.ampmjobs.utility.ActivityUtilities;

public class ConditionalFilter implements HandlerInterceptor {

	private final String contextPath;
	private final boolean isApplicationLive;
	private final String ips;

	public ConditionalFilter(final String userContextPath, final boolean isAppLive, final String userIps) {
		this.contextPath = userContextPath;
		this.isApplicationLive = isAppLive;
		this.ips = userIps;
	}

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {

		String ipAddress = ActivityUtilities.getIPAddress(request);

		List<String> allowedIps = Arrays.asList(ips.split(","));

		HttpServletRequest req = request;
		HttpServletResponse res = response;
		String requestURI = req.getRequestURI();

		if (!requestURI.contains("/assets/") && !requestURI.startsWith(contextPath + "/ip")
				&& !requestURI.startsWith(contextPath + "/construction") && !isApplicationLive
				&& !allowedIps.contains(ipAddress)) {
			res.sendRedirect(contextPath + "/construction");
			return false;
		} else if (requestURI.startsWith(contextPath + "/employer/act/")) {
			return handleEmployerRequest(req, res);
		} else if (requestURI.startsWith(contextPath + "/student/act/")) {
			return handleStudentRequest(req, res);
		} else {
			return true;
		}
	}

	private boolean handleStudentRequest(final HttpServletRequest req, final HttpServletResponse res)
			throws IOException, ServletException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {

			AuthorizedUser user = (AuthorizedUser) authentication.getPrincipal();
			if ("STUDENT".equals(user.getRole())) {

				if (!user.isPaymentCompleted()) {
					res.sendRedirect(contextPath + "/student/getPaymentInfo");
					return false;
				}

				if (!user.isAddtionDetailsFilled()) {
					res.sendRedirect(contextPath + "/student/getAddInfo");
					return false;
				}

			}
		}

		return true;

	}

	private boolean handleEmployerRequest(final HttpServletRequest req, final HttpServletResponse res)
			throws IOException, ServletException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {

			AuthorizedUser user = (AuthorizedUser) authentication.getPrincipal();

			if ("EMPLOYER".equals(user.getRole())) {

				if (!user.isPaymentCompleted()) {
					res.sendRedirect(contextPath + "/employer/getPaymentInfo");
					return false;
				}

				if (!user.isAddtionDetailsFilled()) {
					res.sendRedirect(contextPath + "/employer/getAddInfo");
					return false;
				}
			}

		}
		return true;
	}

}
