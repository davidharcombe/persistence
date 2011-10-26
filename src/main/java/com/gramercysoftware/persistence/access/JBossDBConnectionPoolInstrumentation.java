/**
 * 
 */
package com.gramercysoftware.persistence.access;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

/**
 * @author vkalyanasundaram Used for getting DB connection info from Jboss using
 *         JMX
 */
public class JBossDBConnectionPoolInstrumentation implements
		DBConnectionPoolInstrumentation {

	private Logger log = Logger.getLogger("com.twinlion.access.JBossDBConnectionPoolInstrumentation");

	private static String OBJECT_NAME_PREFIX = "jboss.jca:name=";
	private static String OBJECT_NAME_SUFFIX = ",service=ManagedConnectionPool";

	private static String AVAILABLE_CONNECTION_COUNT = "AvailableConnectionCount";
	private static String INUSE_CONNECTION_COUNT = "InUseConnectionCount";
	private static String CONNECTION_COUNT = "ConnectionCount";
	private static String MAX_CONNECTIONS_INUSE_COUNT = "MaxConnectionsInUseCount";
	private static String MAX_SIZE = "MaxSize";
	private static String MIN_SIZE = "MinSize";
	private static String IDLE_TIMEOUT_MINUTES = "IdleTimeoutMinutes";

	private MBeanServer mbeanServer = null;
	private ObjectName dsObjectName = null;

	public String getObjectName(String dsName) {
		return OBJECT_NAME_PREFIX + dsName + OBJECT_NAME_SUFFIX;
	}

	public JBossDBConnectionPoolInstrumentation(String dsName) {
		init(dsName);
	}

	public void init(String dsName) {
		try {
			// MBeanServer
			mbeanServer = (MBeanServer) MBeanServerFactory
					.findMBeanServer(null).get(0);
			String objName = getObjectName(dsName);
			log.info("ObjName--->" + objName);
			dsObjectName = new ObjectName(objName);

		} catch (Exception e) {
			log
					.error(
							"Exception while initalizing JBoss DB Connection Pool Instrumentation",
							e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.twinlion.access.DBConnectionPoolInstrumentation#
	 * getAvailableConnectionCount()
	 */
	public int getAvailableConnectionCount() {
		// TODO Auto-generated method stub

		int availableConnectionCount = 0;
		try {
			availableConnectionCount = ((Long) mbeanServer.getAttribute(
					dsObjectName, AVAILABLE_CONNECTION_COUNT)).intValue();
		} catch (Exception e) {
			log
					.error(
							"Exception while getting DB connection info from Jboss Server",
							e);
		}
		return availableConnectionCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.twinlion.access.DBConnectionPoolInstrumentation#getInUseConnectionCount
	 * ()
	 */
	public int getInUseConnectionCount() {
		// TODO Auto-generated method stub

		int inUseConnectionCount = 0;
		try {
			inUseConnectionCount = ((Long) mbeanServer.getAttribute(
					dsObjectName, INUSE_CONNECTION_COUNT)).intValue();
		} catch (Exception e) {
			log
					.error(
							"Exception while getting DB connection info from Jboss Server",
							e);
		}
		return inUseConnectionCount;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.twinlion.access.DBConnectionPoolInstrumentation#getConnectionCount()
	 */
	public int getConnectionCount() {
		// TODO Auto-generated method stub
		int connectionCount = 0;
		try {
			connectionCount = ((Integer) mbeanServer.getAttribute(dsObjectName,
					CONNECTION_COUNT)).intValue();
		} catch (Exception e) {
			log
					.error(
							"Exception while getting DB connection info from Jboss Server",
							e);

		}
		return connectionCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.twinlion.access.DBConnectionPoolInstrumentation#
	 * getMaxConnectionsInUseCount()
	 */
	public int getMaxConnectionsInUseCount() {
		// TODO Auto-generated method stub

		int maxConnectionsInUseCount = 0;

		try {
			maxConnectionsInUseCount = ((Long) mbeanServer.getAttribute(
					dsObjectName, MAX_CONNECTIONS_INUSE_COUNT)).intValue();
		} catch (Exception e) {
			log
					.error(
							"Exception while getting DB connection info from Jboss Server",
							e);
		}
		return maxConnectionsInUseCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.twinlion.access.DBConnectionPoolInstrumentation#getMaxSize()
	 */
	public int getMaxSize() {
		// TODO Auto-generated method stub
		int maxSize = 0;

		try {
			maxSize = ((Integer) mbeanServer.getAttribute(dsObjectName,
					MAX_SIZE)).intValue();
		} catch (Exception e) {
			log
					.error(
							"Exception while getting DB connection info from Jboss Server",
							e);
		}

		return maxSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.twinlion.access.DBConnectionPoolInstrumentation#getMinSize()
	 */
	public int getMinSize() {
		// TODO Auto-generated method stub
		int minSize = 0;
		try {
			minSize = ((Integer) mbeanServer.getAttribute(dsObjectName,
					MIN_SIZE)).intValue();
		} catch (Exception e) {
			log
					.error(
							"Exception while getting DB connection info from Jboss Server",
							e);
		}

		return minSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.twinlion.access.DBConnectionPoolInstrumentation#getIdleTimeoutMinutes
	 * ()
	 */
	public long getIdleTimeoutMinutes() {
		// TODO Auto-generated method stub
		long idleTimeoutMinutes = 0;

		try {
			idleTimeoutMinutes = ((Long) mbeanServer.getAttribute(dsObjectName,
					IDLE_TIMEOUT_MINUTES)).longValue();
		} catch (Exception e) {
			log
					.error(
							"Exception while getting DB connection info from Jboss Server",
							e);
		}

		return idleTimeoutMinutes;
	}

}
