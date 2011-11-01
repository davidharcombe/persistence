package com.gramercysoftware.persistence.util;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

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

	@SuppressWarnings("rawtypes")
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
		return new GSPEntityTransaction(em.getTransaction());
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

	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		return em.find(entityClass, primaryKey, properties);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		return em.find(entityClass, primaryKey, lockMode);
	}

	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		return em.find(entityClass, primaryKey, lockMode, properties);
	}

	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		em.lock(entity, lockMode, properties);
	}

	public void refresh(Object entity, Map<String, Object> properties) {
		em.refresh(entity, properties);
	}

	public void refresh(Object entity, LockModeType lockMode) {
		em.refresh(entity, lockMode);
	}

	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		em.refresh(entity, lockMode, properties);
	}

	public void detach(Object entity) {
		em.detach(entity);
	}

	public LockModeType getLockMode(Object entity) {
		return em.getLockMode(entity);
	}

	public void setProperty(String propertyName, Object value) {
		em.setProperty(propertyName, value);
	}

	public Map<String, Object> getProperties() {
		return em.getProperties();
	}

	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		return em.createQuery(criteriaQuery);
	}

	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return em.createQuery(qlString, resultClass);
	}

	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		return em.createNamedQuery(name, resultClass);
	}

	public <T> T unwrap(Class<T> cls) {
		return em.unwrap(cls);
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return em.getEntityManagerFactory();
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return em.getCriteriaBuilder();
	}

	public Metamodel getMetamodel() {
		return em.getMetamodel();
	}
}
