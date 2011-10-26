package com.gramercysoftware.persistence.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Base class for managing entity manager using thread local implementation 
 */
abstract public class EntityManagerUtil {
	protected static String PERSISTENCE_UNIT_KEY = "persistence_unit_name";
	protected  EntityManagerFactory entityManagerFactory ;
	public static final ThreadLocal<EntityManager> entitymanager = new ThreadLocal<EntityManager>();
	
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public EntityManager getEntityManager() {
		EntityManager em = entitymanager.get();

		// Create a new EntityManager
		if (em == null || !em.isOpen()) {
			em = getEntityManagerFactory().createEntityManager();
			em = new GSPEntityManager(em);
			entitymanager.set(em);
		}
		return em;
	}

	public  void closeEntityManager() {
		EntityManager em = entitymanager.get();
		entitymanager.set(null);
		if (em != null && em.isOpen()) {
			em.close();
			em = null;
		}
	}

}
