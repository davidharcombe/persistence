/*
 * Copyright (C) 2011 David Harcombe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
