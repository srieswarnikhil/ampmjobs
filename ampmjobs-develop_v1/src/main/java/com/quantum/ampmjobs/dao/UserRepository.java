package com.quantum.ampmjobs.dao;

public interface UserRepository {

	String getUniqueResult(String query);

	Object[] getMultiResult(String tableName, String email, long mobile);

	Object[] getShortListedInfo(String query);

}
