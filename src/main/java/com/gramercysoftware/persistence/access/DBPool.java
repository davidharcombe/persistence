package com.gramercysoftware.persistence.access;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * An adapter class for DataSource A Datasource implementation to be used along
 * with Application sever pooling features
 * 
 */

public class DBPool implements DataSource {
	private static final Logger log = Logger.getLogger(DBPool.class);

	private DBConnectionPoolInstrumentation dbConnectionPoolInstrumentation;
	private DataSource ds = null;
	private Context ctx = null;
	private String poolName;

	private static Map<String, String> dataSourceMap = new HashMap<String, String>();

	private static String JAVA_COMP_PREFIX = "java:comp/env/";
	private static int JAVA_COMP_PREFIX_LEN = JAVA_COMP_PREFIX.length();

	static {
		dataSourceMap.put("main", "java:comp/env/jdbc/Main");
	}

	public int getMaxConnections() {
		return dbConnectionPoolInstrumentation.getMaxSize();

	}

	public int getActiveConnections() {
		return dbConnectionPoolInstrumentation.getInUseConnectionCount();
	}

	public int getTotalConnections() {
		return dbConnectionPoolInstrumentation.getConnectionCount();
	}

	public int getAvailableConnections() {
		return dbConnectionPoolInstrumentation.getAvailableConnectionCount();
	}

	/**
	 * To display all the pool related attributes
	 */
	public void displayCurrentPoolDetails() {
		log.info("For the datasource : " + dataSourceMap.get(poolName)
				+ " , The current pool info ::  ");

		log.info(" Max Pool Size   -->"
				+ dbConnectionPoolInstrumentation.getMaxSize());
		log.info(" Min Pool Size -->"
				+ dbConnectionPoolInstrumentation.getMinSize());
		log.info(" In Use Connection Count -->"
				+ dbConnectionPoolInstrumentation.getInUseConnectionCount());
		log
				.info(" Max Connections In Use Count -->"
						+ dbConnectionPoolInstrumentation
								.getMaxConnectionsInUseCount());
		log.info(" Connection Count -->"
				+ dbConnectionPoolInstrumentation.getConnectionCount());
		log
				.info(" Available Connection Count -->"
						+ dbConnectionPoolInstrumentation
								.getAvailableConnectionCount());
		log.info("Idle Timeout Minutes -->"
				+ dbConnectionPoolInstrumentation.getIdleTimeoutMinutes());

	}

	public Connection getConnection() {
		try {
			log.debug("getting connection for poolName :"
					+ dataSourceMap.get(poolName));

			return ds.getConnection();
		} catch (SQLException ex) {
			log.error(
					"could not get a connection. your system may be in a very bad state:"
							+ ex, ex);
			return null;
		}
	}

	private void lookUpDataSource(String poolName) {

		String dsName = (String) dataSourceMap.get(poolName);

		try {
			ctx = new javax.naming.InitialContext();
			log.info("Datasource name :" + dsName);
			ds = (DataSource) ctx.lookup(dsName);
			log.info("Datasource for :" + dsName + "=" + ds);

		} catch (Exception ex) {
			log.error("could not get DataSource " + ds + " ex=" + ex
					+ " with dsName: " + dsName, ex);
		}

		dbConnectionPoolInstrumentation = new JBossDBConnectionPoolInstrumentation(
				dsName.substring(JAVA_COMP_PREFIX_LEN));
	}

	void setName(String poolName) {
		this.poolName = poolName;
		log.info("poolname :" + poolName);
		lookUpDataSource(poolName);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return ds.getLogWriter();
	}

	public int getLoginTimeout() throws SQLException {
		return ds.getLoginTimeout();
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		ds.setLoginTimeout(seconds);
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		ds.setLogWriter(out);
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		return ds.getConnection(username, password);
	}

	public void finalize() {
		try {
			ds = null;
			if (ctx != null) {
				ctx.close();
			}
		} catch (Exception ex) {
			log.warn("could not close DataSource "
					+ dataSourceMap.get(poolName) + " ex=" + ex, ex);
		}
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return ds.isWrapperFor(arg0);
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return ds.unwrap(arg0);
	}
}
