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

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;

public class JpaJdbcConnectionJoiner<T> {
	private EntityManager entityManager;
	private T response;
	private Exception exception;

	public JpaJdbcConnectionJoiner(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void doWork(final JdbcWork<T> jdbcWork)  {
		exception = null;
		Session session = null;
		
		if (entityManager instanceof GSPEntityManager) {
			session = ((HibernateEntityManager) ((GSPEntityManager)entityManager).getWrappedEntityManager()).getSession();
		} else {
			session = ((HibernateEntityManager) entityManager).getSession();
		}
		
		session.doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				try {
					response = jdbcWork.execute(connection);
				} catch (SQLException sqlex) {
					throw sqlex;
 				} catch (Exception t) {
 					exception = t;
				}
			}
		});
		
		if (exception != null) {
			throw new RuntimeException(exception);
		}
	}
	
	public T getResponse() {
		return response;
	}
}
