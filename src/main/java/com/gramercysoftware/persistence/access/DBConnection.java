package com.gramercysoftware.persistence.access;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;
import com.gramercysoftware.persistence.util.GSPEntityManager;

/**
 * Connection.java Created on Feb 13, 2003
 */
public class DBConnection implements java.sql.Connection {
	private static Logger log = Logger.getLogger(DBConnection.class);
	protected List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	protected java.sql.Connection c = null;
	protected String id = System.currentTimeMillis() + "." + this.hashCode();
	protected boolean readOnly = false;
	boolean autoCommit = true;

	// to identify offending caller class
	protected Throwable throwable;

	// to manage un-returned database connections that have been expired
	protected DBConnectionManager connectionManager = DBConnectionManager.getInstance();
	private GSPEntityManager entityManager = null;

	private void init(java.sql.Connection c, boolean readOnly, long maxLeaseTime) throws SQLException {
		this.c = c;

		// help to identify offending caller class who didn't return
		// db connections back to pool
		throwable = new Throwable();

		autoCommit = c.getAutoCommit();
		log.debug("Connection " + id + " created.");

		this.readOnly = readOnly;
		if (readOnly) {
			c.setAutoCommit(false);
		}
		// add this connection to manager for monitoring
		connectionManager.addConnection(this);
	}

	private DBConnection(java.sql.Connection c) throws SQLException {
		init(c, false, DatabasePool.DEFAULT_MAX_LEASE);
	}

	private DBConnection(java.sql.Connection c, boolean readOnly) throws SQLException {
		init(c, readOnly, DatabasePool.DEFAULT_MAX_LEASE);
	}

	/**
	 * @param c
	 * @param readOnly
	 * @param maxLeaseTime
	 *            maximum lease time of this connection in milliseconds
	 * @throws SQLException
	 */
	public DBConnection(java.sql.Connection c, boolean readOnly, long maxLeaseTime) throws SQLException {
		log.debug("connection object of type :" + c.getClass().getName() + " To string :" + c.getClass().getName().toString());
		init(c, readOnly, maxLeaseTime);
	}

	public Throwable getThrowable() {
		return this.throwable;
	}

	public boolean equals(Object object) {
		boolean same = false;
		try {
			DBConnection dbc = (DBConnection) object;
			if (this.id == dbc.id) {
				same = true;
			}
		} catch (ClassCastException cce) {
			// different object type
		}
		return same;
	}

	private GSPEntityManager getManager() {
		if (entityManager == null) {
			entityManager = (GSPEntityManager) DefaultEntityManagerUtil.getInstance().getEntityManager();
		}

		return entityManager;
	}

	/**
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		return c.createStatement();
	}

	/**
	 * @see java.sql.Connection#prepareStatement(String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return c.prepareStatement(sql);
	}

	/**
	 * @see java.sql.Connection#prepareCall(String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return c.prepareCall(sql);
	}

	/**
	 * @see java.sql.Connection#nativeSQL(String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		return c.nativeSQL(sql);
	}

	/**
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		c.setAutoCommit(autoCommit);
	}

	/**
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		return c.getAutoCommit();
	}

	/**
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		if (!readOnly) {
			c.commit();
			log.debug("connection " + id + " committed.");
		}
	}

	private void callCommitListeners() {
		for (ConnectionListener listener : listeners) {
			try {
				listener.connectionCommitted();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	private void callRollBackListeners() {
		for (ConnectionListener listener : listeners) {
			try {
				listener.connectionRolledback();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	private void callCloseListeners() {
		for (ConnectionListener listener : listeners) {

			try {
				log.info("Calling connectionClosed on " + listener.getClass().getName());
				listener.connectionClosed();
				log.info("ConnectionClosed call completed for " + listener.getClass().getName());
			} catch (Exception e) {
				log.error(e);
			}
		}

		listeners.clear();
	}

	/**
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		c.rollback();
		log.debug("connection " + id + " rolledback.");
	}

	/**
	 * @see java.sql.Connection#close()
	 */
	public void close(boolean removeFromManager) throws SQLException {
		if (readOnly) {
			rollback();
		}
		if (!c.isClosed()) {
			c.setAutoCommit(autoCommit);
			c.close();

			if (removeFromManager) {
				connectionManager.removeConnection(this);
			}
		}
	}

	public void close() throws SQLException {
		close(true);
	}

	/**
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return c.isClosed();
	}

	/**
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return c.getMetaData();
	}

	/**
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		c.setReadOnly(readOnly);
	}

	/**
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return c.isReadOnly();
	}

	/**
	 * @see java.sql.Connection#setCatalog(String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		c.setCatalog(catalog);
	}

	/**
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		return c.getCatalog();
	}

	/**
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		c.setTransactionIsolation(level);
	}

	/**
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		return c.getTransactionIsolation();
	}

	/**
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return c.getWarnings();
	}

	/**
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		c.clearWarnings();
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return c.createStatement(resultSetType, resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return c.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#prepareCall(String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return c.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return c.getTypeMap();
	}

	/**
	 * @see java.sql.Connection#setTypeMap(Map)
	 */
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		c.setTypeMap(map);
	}

	/**
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		c.setHoldability(holdability);
	}

	/**
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		return c.getHoldability();
	}

	/**
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		return c.setSavepoint();
	}

	/**
	 * @see java.sql.Connection#setSavepoint(String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		return c.setSavepoint(name);
	}

	/**
	 * @see java.sql.Connection#rollback(Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		c.rollback(savepoint);
	}

	/**
	 * @see java.sql.Connection#releaseSavepoint(Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		c.releaseSavepoint(savepoint);
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return c.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
		throws SQLException {
		return c.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareCall(String, int, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
		throws SQLException {
		return c.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return c.prepareStatement(sql, autoGeneratedKeys);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return c.prepareStatement(sql, columnIndexes);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(String, String[])
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return c.prepareStatement(sql, columnNames);
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return c.createArrayOf(typeName, elements);
	}

	public Blob createBlob() throws SQLException {
		return c.createBlob();
	}

	public Clob createClob() throws SQLException {
		return c.createClob();
	}

	public NClob createNClob() throws SQLException {
		return c.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return c.createSQLXML();
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return c.createStruct(typeName, attributes);
	}

	public Properties getClientInfo() throws SQLException {
		return c.getClientInfo();
	}

	public String getClientInfo(String name) throws SQLException {
		return c.getClientInfo(name);
	}

	public boolean isValid(int timeout) throws SQLException {
		return c.isValid(timeout);
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		c.setClientInfo(properties);

	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		c.setClientInfo(name, value);
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return c.isWrapperFor(arg0);
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return c.unwrap(arg0);
	}
}