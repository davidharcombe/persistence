package com.gramercysoftware.persistence.access;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * Represents a pool of database connections.
 *
 *
 *
 */
public class DatabasePool extends DBPool {
    private static Logger log = Logger.getLogger( DatabasePool.class );

    // Default parameters
    private static final String POOL_NAME = "main";

	private static Hashtable<String, DatabasePool> pools = new Hashtable<String, DatabasePool>();

    // default maximum connection lease time is 10 seconds
    public static final long DEFAULT_MAX_LEASE = 10*1000;
    private long maxLeaseTime = DEFAULT_MAX_LEASE;
    
    private static DatabasePool testDatabasePool;

    /**
     * @deprecated - Testing only - Required for mock testing
     */
    public DatabasePool() {
    }
    
    protected static void setTestInstance(DatabasePool pool) {
    	testDatabasePool = pool;
    }
    
    protected static void resetTestInstance() {
    	testDatabasePool = null;
    }
     
    private DatabasePool(String poolName ) throws SQLException {
        super();
        this.setName(poolName);
		log.info( "DatabasePool initialized.");
    }

	public static DatabasePool createPool(String poolName) {
		if (poolName != null) {
			DatabasePool pool =  (DatabasePool) pools.get(poolName);

			if (pool == null) {
				try {
					pool = new DatabasePool(poolName);
					pools.put( poolName, pool );
					log.info("New pool created with name["
    						 + poolName + "] and it has "
    						 + pool.getTotalConnections()
    						 + " connections." );
				} catch (SQLException se) {
					log.error( "unable to create dbpool", se );
					System.err.println("Unable to establish DB connection");
				}
			}

			return pool;
		} else {
			throw new IllegalArgumentException( "pool name was null" );
		}
	}

    /**
     * returns an instance of the default pool specified
     * in the DBProperties object.
     */
    public static DatabasePool getInstance() {
    	if (testDatabasePool != null) {
    		return testDatabasePool;
    	}
    	
		//Hashtable pools = getPools();
        DatabasePool defaultPool = pools.get(POOL_NAME);
        if (defaultPool == null) {
            defaultPool = createPool(POOL_NAME);
        }
        return defaultPool;
    }
    
    /**
     * @return the database pool named poolName. If none found null is returned.
     */
    public static DatabasePool getInstance( String poolName ) {
        if ( poolName != null ) {
            return (DatabasePool)pools.get(poolName);
        }else {
            throw new IllegalArgumentException( "pool name was null" );
        }
    }

    public Connection getConnection() {
        return getConnection(maxLeaseTime);
    }

	private String gethostName()  {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "[unable to retrieve host name]";
		}
	}
    /**
     * @param leaseTime  number of milliseconds before the connection is closed by
     *                   connection manager
     * @return
     */
    public Connection getConnection(long leaseTime) {
        Connection c = null;
        
        int connectionAcquisitionAttempt = 0;
	    while (c == null) {
	    	log.debug("connectionAcquisitionAttempt=" + connectionAcquisitionAttempt);
	    	if (connectionAcquisitionAttempt > 1) {
	    		log.debug("Going to sleep for EVER");
	    		sleepForever();
	    		return null;
	    	}
	    	
	        try {
                c = new DBConnection(super.getConnection(), false, leaseTime);
                log.debug(" created new DBConnection object ..");
	        } catch (Throwable se) {
	        	try {
	        		log.debug("Going to sleep for 1000 milliseconds");
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
                log.error( "Max connections reached. Caught Exception ", se);
	        }
	        
	        connectionAcquisitionAttempt++;
	    }
       
	    return c;
    }
    

    /**
     * This will make the thread sleep forever and never return. Obviously it's not a good thing
     * and will be removed once we move away from Resin's database pooling.
     * 
     * The reason behind why the thread will sleep forever is that prior to logging the stack trace
     * of all the database connections, the system would spin in an infinite look anyways trying to acquire
     * connections and logging until the system crashed. If we make this thread sleep forever, the behaviour
     * will remain consistent and we will have the logs available which will help us in finding out
     * which connections are leaking.
     */
    private void sleepForever() {
		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				log.error("This thread was meant to sleep forever", e);
			}
		}
		 
	}

	/**
	 * @deprecated - Testing only
	 */
	public static Hashtable<String, DatabasePool> getPools() {
		return pools;
	}
}
