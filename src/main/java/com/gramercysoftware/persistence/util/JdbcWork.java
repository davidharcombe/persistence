package com.gramercysoftware.persistence.util;

import java.sql.Connection;

public interface JdbcWork<T> {
	public T execute(Connection connection) throws Exception;
}
