package com.gramercysoftware.persistence.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.gramercysoftware.persistence.access.ConnectionListener;

public class GSPEntityManager implements EntityManager {
	private Logger logger = Logger.getLogger(GSPEntityManager.class);
	
	protected List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	
	private final EntityManager em;
	
	private boolean hasCloseListeneresBeenCalled ;
	private boolean hasCommitListeneresBeenCalled ;
	private boolean hasRollbackListeneresBeenCalled ;
	
	public GSPEntityManager(EntityManager em) {
		this.em = em;
		hasCloseListeneresBeenCalled = false;
		hasCommitListeneresBeenCalled = false;
		hasRollbackListeneresBeenCalled = false;
	}

	public void clear() {
		em.clear();
	}

	public void addListener(ConnectionListener listener) {
		listeners.add(listener);
	}
	
	public List<ConnectionListener> getListeners() {
		return listeners;
	}
	
	public void close() {
		logger.debug("About to close EntityManager");
		em.close();
		logger.debug("Entity Manager is closed");
		
		callCloseListeners();
	}

	public void callCloseListeners() {
		
		if (!hasCloseListeneresBeenCalled) {
			hasCloseListeneresBeenCalled = true;
			for (ConnectionListener listener : listeners) {
				
	            try {
	            	logger.debug("Calling connectionClosed on "+listener.getClass().getName());
	                listener.connectionClosed();
	                logger.debug("ConnectionClosed call completed for "+listener.getClass().getName());
	            }catch( Exception e ) {logger.error(e);}
	        }
			
	        listeners.clear();
		} else {
			logger.debug("close listeners multiple times - operation will be ignored.");
		}
	}
	
	public void callCommitListeners() {
	
		if (!hasCommitListeneresBeenCalled) {
			
			
			hasCommitListeneresBeenCalled = true;
			
			GSPEntityTransaction transaction = (GSPEntityTransaction) getTransaction();
			transaction.callCommitListeners();
		} else {
			logger.debug("Commit listeners multiple times - operation will be ignored.");
		}
	}
	
	public void callRollBackListeners() {
		if (!hasRollbackListeneresBeenCalled) {
			hasRollbackListeneresBeenCalled = true;
			
			GSPEntityTransaction transaction = (GSPEntityTransaction) getTransaction();
			transaction.callRollbackListeners();
		} else {
			logger.debug("Rollback listeners multiple times - operation will be ignored.");
		}
		
	}
	
	public EntityManager getWrappedEntityManager() {
		return em;
	}

	public boolean contains(Object entity) {
		return em.contains(entity);
	}

	public Query createNamedQuery(String name) {
		return em.createNamedQuery(name);
	}

	public Query createNativeQuery(String sqlString, Class resultClass) {
		return em.createNativeQuery(sqlString, resultClass);
	}

	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return em.createNativeQuery(sqlString, resultSetMapping);
	}

	public Query createNativeQuery(String sqlString) {
		return em.createNativeQuery(sqlString);
	}

	public Query createQuery(String ejbqlString) {
		return em.createQuery(ejbqlString);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return em.find(entityClass, primaryKey);
	}

	public void flush() {
		em.flush();
	}

	public Object getDelegate() {
		return em.getDelegate();
	}

	public FlushModeType getFlushMode() {
		return em.getFlushMode();
	}

	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return em.getReference(entityClass, primaryKey);
	}

	public EntityTransaction getTransaction() {
		return new GSPEntityTransaction(em.getTransaction(), this);
	}

	public boolean isOpen() {
		return em.isOpen();
	}

	public void joinTransaction() {
		em.joinTransaction();
	}

	public void lock(Object entity, LockModeType lockMode) {
		em.lock(entity, lockMode);
	}

	public <T> T merge(T entity) {
		return em.merge(entity);
	}

	public void persist(Object entity) {
		em.persist(entity);
	}

	public void refresh(Object entity) {
		em.refresh(entity);
	}

	public void remove(Object entity) {
		em.remove(entity);
	}

	public void setFlushMode(FlushModeType flushMode) {
		em.setFlushMode(flushMode);
	}
	
	
}
