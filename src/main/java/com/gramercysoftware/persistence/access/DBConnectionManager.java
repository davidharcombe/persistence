package com.gramercysoftware.persistence.access;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * <p>Title: DBConnectionManager</p>
 * 
 * <p>Description: </p>
 * 
 * <p>Copyright: Copyright (c) 2002</p>
 * 
 * <p>Company: </p>
 * 
 * @author tsiu
 * @version 1.0
 */
public class DBConnectionManager {

	private static Logger log = Logger.getLogger(DBConnectionManager.class);

	// this is thread safe as static final variables
	// are guaranteed to be initialized before the class
	// can be used.  Helpful tip from the
	// Doug Lea book.

	private static final DBConnectionManager instance = new DBConnectionManager();


    private List<Connection> activeConnections;

    private int maxStackElement;

    private static final String indent = "     ";
    
    private static Map<String, Exception> CONNECTION_TRACE  = new HashMap<String, Exception>(); // map of connection ID and Stack Trace

    public static DBConnectionManager getInstance() {

        return instance;
    }

    private DBConnectionManager() {
        activeConnections = new ArrayList<Connection>();

        ResourceBundle bundle =
            ResourceBundle.getBundle(this.getClass().getName(), Locale.getDefault());

        String maxStackElementStr = bundle.getString("maxStackElement");
        maxStackElement = Integer.parseInt(maxStackElementStr);
    }
    
    public static String logDbConnectionTrace() {
    	StringBuffer message = new StringBuffer();
    	message.append("There are " + CONNECTION_TRACE.size() + " open connections at this time\n");
    	message.append("-------------------------------------------------------------------------\n");
    	message.append("Printing all connection trace back information.\n");
    	
    	for (String databaseConnectionId : CONNECTION_TRACE.keySet()) {
    		StringWriter sw = new StringWriter();
    		Exception exception = (Exception) CONNECTION_TRACE.get(databaseConnectionId);
    		exception.printStackTrace(new PrintWriter(sw));
    		message.append("--------- > Database connection with connection id: " + databaseConnectionId + " has the following stack trace:\n");
    		message.append(sw.toString() + "\n");
    	}
    	
    	message.append("-------------------------------------------------------------------------");
    	log.error(message.toString());
    	return message.toString();
    }

    public synchronized void addConnection(DBConnection dbc) {
        log.debug(">>>>> Adding connection " + dbc.id + " to manager, "
        		+ "activeConnection pool size=[" + (activeConnections.size() + 1) + "]");
        activeConnections.add(dbc);
        
        CONNECTION_TRACE.put(dbc.id, new Exception());
    }

    public synchronized void removeConnection(DBConnection dbc) {
        log.debug("<<<<< Removing connection " + dbc.id + " from manager, "
        		+ "activeConnection pool size=[" + (activeConnections.size() - 1) + "]");
        activeConnections.remove(dbc);
        
        CONNECTION_TRACE.remove(dbc.id);
    }
    
    public String getOffendingClassInfo(Throwable throwable) {
        StringBuffer sb = new StringBuffer("\nOffending class info:\n");
        StackTraceElement[] elements = throwable.getStackTrace();

        // No need to go thru the whole stack as the offending class should be
        // around the 6th element
        for(StackTraceElement elem : elements) {
            sb.append(indent + elem.getClassName() + ":" + elem.getMethodName() + " line(" + elem.getLineNumber() + ")\n");
        }

        sb.append("\nSite url is: ");
        sb.append(getHostName());
        return sb.toString();
    }

    private String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "[unable to get host name]";
		}
	}
}