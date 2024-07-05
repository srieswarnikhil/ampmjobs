package com.quantum.ampmjobs.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.quantum.ampmjobs.utility.ActivityUtilities;

@Configuration
public class DatabaseConfig {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${jasypt.encryptor.password}")
	private String encKey;

	@Bean
	public DataSource dataSource() {
		String userName = ActivityUtilities.getComponentValue(username, encKey);
		String pwd = ActivityUtilities.getComponentValue(password, encKey);
		return DataSourceBuilder.create().url(url).username(userName).password(pwd).build();
	}

}
