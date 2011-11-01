/*
 * Simple pooled DatSource Factory
 * Copyright (C) 2011 David Harcombe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gramercysoftware.persistence.dao;

import java.io.IOException;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class GSPDataSourceFactory {
	private static Logger logger = Logger.getLogger(GSPDataSourceFactory.class);
	private PoolingDataSource dataSource;
	private String propertyFileName;

	public GSPDataSourceFactory() {
	}
	
	public GSPDataSourceFactory(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}
	
	public DataSource getDataSource() {
		if(dataSource == null) {
			createDataSource();
		}
		
		return dataSource;
	}
	
	private void createDataSource() {
		Properties properties = getDataSourceConfig();
		initializeDatabaseDriver(properties);
		
		ObjectPool connectionPool = createConnectionPool(properties);
		ConnectionFactory connectionFactory = 
			new DriverManagerConnectionFactory(properties.getProperty("jdbc.url"), properties.getProperty("jdbc.user"), properties.getProperty("jdbc.password"));
		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true, Connection.TRANSACTION_SERIALIZABLE);
		dataSource = new PoolingDataSource(connectionPool);
	}

	private ObjectPool createConnectionPool(Properties properties) {
		GenericObjectPool.Config config = new GenericObjectPool.Config();
		config.maxActive = Integer.parseInt(properties.getProperty("pool.maxActive"));
		config.maxIdle = Integer.parseInt(properties.getProperty("pool.maxIdle"));
		config.minIdle = Integer.parseInt(properties.getProperty("pool.minIdle"));
		config.maxWait = Integer.parseInt(properties.getProperty("pool.maxWait"));

		ObjectPool connectionPool = new GenericObjectPool(null, config);
		return connectionPool;
	}

	private Properties getDataSourceConfig() {
		Properties properties = new Properties();
		try {
			properties.load(GSPDataSourceFactory.class.getResourceAsStream(getPropertyFileName()));
		} catch (IOException e) {
			logger.error(MessageFormat.format("Error loading property file {0}", getPropertyFileName()), e);
		}
		
		return properties;
	}

	private void initializeDatabaseDriver(Properties properties) {
		try {
			Class.forName(properties.getProperty("jdbc.driver"));
		} catch (ClassNotFoundException e) {
			logger.error(MessageFormat.format("Error initializing JDBC driver {0}", properties.getProperty("jdbc.driver")), e);
		}
	}

	public String getPropertyFileName() {
		if(propertyFileName == null) {
			propertyFileName = GSPDataSourceFactory.class.getSimpleName();
		}
		return propertyFileName;
	}

	@Deprecated
	void setPropertyFileName(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}
}
