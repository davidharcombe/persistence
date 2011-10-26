/**
 * 
 */
package com.gramercysoftware.persistence.access;

/**
 * @author vkalyanasundaram
 *
 */
public interface DBConnectionPoolInstrumentation {

	public int getMaxConnectionsInUseCount();
	public int getConnectionCount();
	public int getAvailableConnectionCount();
	public int getInUseConnectionCount();
	
	public int getMaxSize();
	public int getMinSize();
	
	public long getIdleTimeoutMinutes();
	
	
	
}
