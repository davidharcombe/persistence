package com.gramercysoftware.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.gramercysoftware.persistence.dao.GSPDataSourceFactory;
import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;

public abstract class DatabaseTestCase {
	private static DataSource dataSource;
	static {
		dataSource = new GSPDataSourceFactory("/test_database_config.properties").getDataSource();
	}

	@Before
	public void setUpData() throws Exception {
		cleanupTransaction();
		Logger.getRootLogger().setLevel(Level.ERROR);
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
			if (c != null)
				try {
					c.close();
				} catch (Exception ex) {
				}
		}
	}

	@SuppressWarnings("unused")
	private void restoreForeignKeyConstraints(Connection c) throws SQLException {
		c.createStatement().execute("PRAGMA foreign_keys = ON;");
	}

	@SuppressWarnings("unused")
	private void removeForeignKeyConstraints(Connection c) throws SQLException {
		c.createStatement().execute("PRAGMA foreign_keys = OFF;");
	}

	private void cleanTable(Connection c, String tableName) throws SQLException {
		c.createStatement().execute("delete from " + tableName);
	}
}
