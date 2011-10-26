package com.gramercysoftware.persistence.dao;

import java.io.Serializable;
import java.util.List;

import com.gramercysoftware.persistence.transaction.annotation.Transactional;
import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;
import com.gramercysoftware.persistence.util.EntityManagerUtil;

/**
 * Generic JpaDao implementation. Currently has the methods annotated with @Transactional
 * meaning it will executed within a transaction.
 * 
 * @param <T>
 * @param <ID>
 */
public class GenericJpaDao<T, ID extends Serializable> implements GenericDao<T, ID> {
	private Class<T> type;
	private EntityManagerUtil entityManagerUtil;

	public GenericJpaDao(Class<T> type) {
		this(type, DefaultEntityManagerUtil.getInstance());
	}

	public GenericJpaDao(Class<T> type, EntityManagerUtil entityManagerUtil) {
		this.type = type;
		this.entityManagerUtil = entityManagerUtil;
	}

	public EntityManagerUtil getEntityManagerUtil() {
		return entityManagerUtil;
	}

	public void setEntityManagerUtil(EntityManagerUtil entityManagerUtil) {
		this.entityManagerUtil = entityManagerUtil;
	}

	@Transactional
	public void delete(T entity) {
		entityManagerUtil.getEntityManager().remove(entity);
	}

	@Transactional
	public T get(ID id) {
		if (id == null) {
			return null;
		} else {
			return entityManagerUtil.getEntityManager().find(type, id);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<T> getAll() {
		return entityManagerUtil.getEntityManager().createQuery("select o from " + type.getName() + " o ").getResultList();
	}

	@Transactional
	public T save(T entity) {
		preprocess(entity);
		return entityManagerUtil.getEntityManager().merge(entity);
	}

	@Transactional
	public void insert(T entity) {
		preprocess(entity);
		entityManagerUtil.getEntityManager().persist(entity);
	}

	protected T preprocess(T entity) {
		return entity;
	}
}
