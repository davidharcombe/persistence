package com.gramercysoftware.persistence.access;

import static junit.framework.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;

public abstract class DatabaseTestCase {
	private static PoolingDataSource dataSource;
	static {
		Properties properties = new Properties();
		try {
			properties.load(DatabaseTestCase.class.getResourceAsStream("/test_database_config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
		try {
			Class.forName(properties.getProperty("jdbcDriver"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		String url = properties.getProperty("jdbcURL");
		String username = properties.getProperty("jdbcUser");
		String password = properties.getProperty("jdbcPassword");

		GenericObjectPool.Config config = new GenericObjectPool.Config();
		config.maxActive = 150;
		config.maxIdle = 100;
		config.minIdle = 30;
		config.maxWait = 1000;

		ObjectPool connectionPool = new GenericObjectPool(null, config);
		
		//
		// First, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, username, password);

		//
		// Next we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

		//
		// Finally, we create the PoolingDriver itself,
		// passing in the object pool we created.
		//
		dataSource = new PoolingDataSource(connectionPool);
	}

	@Before
	public void setUpData() throws Exception {
		cleanupTransaction();
		Logger.getRootLogger().setLevel(Level.ERROR);
//		configureTestDatabaseConnection();
		deleteAllRecords();
	}

	/**
	 * Sometimes errors in test cases may put the transaction in a bad state.
	 * This resets the transaction so further test cases are not affected.
	 */
	private void cleanupTransaction() {
		if (DefaultEntityManagerUtil.getInstance().getEntityManager().getTransaction().isActive()) {
			try {
				DefaultEntityManagerUtil.getInstance().getEntityManager().getTransaction().commit();
			} catch (Exception e) {
				DefaultEntityManagerUtil.getInstance().getEntityManager().getTransaction().rollback();
			}
		}

		try {
			if (DefaultEntityManagerUtil.getInstance().getEntityManager().isOpen()) {
				DefaultEntityManagerUtil.getInstance().getEntityManager().close();
			}
		} catch (Exception e) {

		}

		try {
			DefaultEntityManagerUtil.getInstance().closeEntityManager();
		} catch (Exception e) {

		}
	}

	@After
	public void resetTestDatabaseConnection() {
	}

	private void deleteAllRecords() throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		Connection c = dataSource.getConnection();
		try {
			// removeForeignKeyConstraints(c);

			rs = c.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				cleanTable(c, rs.getString("TABLE_NAME"));
			}

			// restoreForeignKeyConstraints(c);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ex) {
				}
			if (ps != null)
				try {
					ps.close();
				} catch (Exception ex) {
				}
			if (c != null)
				try {
					c.close();
				} catch (Exception ex) {
				}
		}
	}

	private void restoreForeignKeyConstraints(Connection c) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = c.prepareStatement("PRAGMA foreign_keys = ON;");
			ps.executeUpdate();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (Exception ex) {
				}
		}
	}

	private void removeForeignKeyConstraints(Connection c) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = c.prepareStatement("PRAGMA foreign_keys = OFF;");
			ps.executeUpdate();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (Exception ex) {
				}
		}
	}

	private void cleanTable(Connection c, String tableName) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = c.prepareStatement("delete from " + tableName);
			ps.executeUpdate();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (Exception ex) {
				}
		}
	}
}
