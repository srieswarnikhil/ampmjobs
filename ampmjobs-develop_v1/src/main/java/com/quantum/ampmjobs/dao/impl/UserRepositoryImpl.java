package com.quantum.ampmjobs.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.quantum.ampmjobs.dao.UserRepository;

@Repository("userRepo")
public class UserRepositoryImpl implements UserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public String getUniqueResult(final String query) {
		return jdbcTemplate.queryForObject(query, String.class);
	}

	@Override
	public Object[] getMultiResult(final String tableName, final String email, final long phone) {
		String query = "SELECT id, name, photo_path FROM public." + tableName
				+ " WHERE email = ? AND phone = ? LIMIT 1";
		return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
			String id = resultSet.getString("id");
			String name = resultSet.getString("name");
			String photoPath = resultSet.getString("photo_path");
			return new Object[] { id, name, photoPath };
		}, email, phone);
	}

	@Override
	public Object[] getShortListedInfo(final String query) {
		return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
			String phone = resultSet.getString("phone");
			String company_name = resultSet.getString("company_name");
			return new Object[] { phone, company_name };
		});
	}

}
