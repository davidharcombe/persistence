package com.gramercysoftware.persistence.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;

public abstract class DatabaseTestCase {
	@Before
	public void setUpData() throws Exception {
		cleanupTransaction();
		Logger.getRootLogger().setLevel(Level.ERROR);
		configureTestDatabaseConnection();
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
		DatabasePool.resetTestInstance();
	}

	private void deleteAllRecords() throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		Connection c = DatabasePool.getInstance().getConnection();
		try {
//			removeForeignKeyConstraints(c);
			
			rs = c.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
			while (rs.next()) {
				cleanTable(c, rs.getString("TABLE_NAME"));
			}

//			restoreForeignKeyConstraints(c);
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

	private void configureTestDatabaseConnection() {
		try {
			DatabasePool.setTestInstance(new DatabaseTestPool());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
