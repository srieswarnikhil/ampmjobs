package com.quantum.ampmjobs.security.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SelfBeanConfiguration {

	@Bean
	@Qualifier("db.props")
	Properties customProperties() throws IOException {
		ClassPathResource resource = new ClassPathResource("messages.db.properties");
		return PropertiesLoaderUtils.loadProperties(resource);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

}