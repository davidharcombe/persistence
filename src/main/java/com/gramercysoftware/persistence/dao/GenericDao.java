package com.gramercysoftware.persistence.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Generic Interface providing support for basic DB operations
 * 
 * @param <T>
 *            The main entity
 * @param <ID>
 *            Key Identifier type
 */
public interface GenericDao<T, ID extends Serializable> {

	T get(ID id);

	List<T> getAll();

	T save(T entity);

	void delete(T entity);

	void insert(T entity);
}
