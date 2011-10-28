package com.gramercysoftware.persistence.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.apache.log4j.Logger;

public class GSPEntityManager implements EntityManager {
	private Logger logger = Logger.getLogger(GSPEntityManager.class);
	
	private final EntityManager em;
	
	public GSPEntityManager(EntityManager em) {
		this.em = em;
	}

	public void clear() {
		em.clear();
	}

	public void close() {
		logger.debug("About to close EntityManager");
		em.close();
		logger.debug("Entity Manager is closed");
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
