package com.gramercysoftware.persistence.access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.gramercysoftware.persistence.access.DatabasePool;

@SuppressWarnings("deprecation")
public class DatabaseTestPool extends DatabasePool {

	public Connection getConnection() {

		try {
			Properties properties = new Properties();
			properties.load(DatabaseTestPool.class.getResourceAsStream("/test_database_config.properties"));
			
			Class.forName(properties.getProperty("jdbcDriver"));
			String url = properties.getProperty("jdbcURL");
			String username = properties.getProperty("jdbcUser");
			String password = properties.getProperty("jdbcPassword");

			Connection connection =  DriverManager.getConnection(url, username, password);
			
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
