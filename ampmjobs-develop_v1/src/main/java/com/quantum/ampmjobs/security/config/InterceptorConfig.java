package com.quantum.ampmjobs.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.quantum.ampmjobs.filter.ConditionalFilter;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

	@Value("${server.servlet.context-path}")
	private String contextPath;

	@Value("${isApplicationLive}")
	private boolean isApplicationLive;

	@Value("${user-ip}")
	private String userIp;

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new ConditionalFilter(contextPath, isApplicationLive, userIp))
				// .addPathPatterns("/employer/act/**", "/student/act/**");
				.addPathPatterns("/**");
	}
}