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
