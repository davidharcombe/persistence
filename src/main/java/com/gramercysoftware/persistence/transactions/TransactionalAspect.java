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
package com.gramercysoftware.persistence.transactions;

import org.apache.log4j.Logger;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.persistence.EntityManager;

import com.gramercysoftware.persistence.util.EntityManagerUtil;

import javax.persistence.EntityTransaction;

@Aspect
public abstract class TransactionalAspect {
	private Logger log = Logger.getLogger(TransactionalAspect.class);
	private EntityManagerUtil entityManagerUtil;

	@Pointcut
	abstract void transactionalPoint();

	public TransactionalAspect(EntityManagerUtil entityManagerUtil){
		this.entityManagerUtil = entityManagerUtil;
	}

	public void setEntityManagerUtil(EntityManagerUtil entityManagerUtil){
		this.entityManagerUtil = entityManagerUtil;
	}

	@Around("transactionalPoint() && !within(com.enstream.transactions.TransactionalAspect)")
	public Object transactional(ProceedingJoinPoint pjp) throws Throwable {
		EntityManager entityManager = null;
		EntityTransaction entityTransaction =null;
		boolean isActive = true;
		try {
			entityManager = entityManagerUtil.getEntityManager();

			entityTransaction = entityManager.getTransaction();
			isActive = entityTransaction.isActive();

			if (!isActive) {
				log.debug("Starting a database transaction at ------------->" + pjp);
				entityManagerUtil.closeEntityManager();
				entityManager = entityManagerUtil.getEntityManager();
				entityTransaction = entityManager.getTransaction();
				entityTransaction.begin();
			}else{
				log.debug("transaction already active so won't start at  ------------->" + pjp);
			}

			Object result = pjp.proceed();

			// Commit and cleanup
			if (!isActive) {
				boolean rollbackOnly = entityTransaction.getRollbackOnly();

				if (! rollbackOnly){
					log.debug("Committing the database transaction at -------------> " + pjp +",rollbackOnly:" + rollbackOnly );
					entityTransaction.commit();
				} else {
					log.debug("Rolling back the database transaction at ------------->:" + pjp + ",rollbackOnly:" + rollbackOnly );
					entityTransaction.rollback();
				}
			}else{
				log.debug(" transaction already active so won't commit at this stage at -------------> " + pjp);
			}
			return result;
		} catch (Throwable ex) {
			// Rollback only
			try {
				if (entityTransaction != null ) {
					log.debug(" Trying to rollback database transaction after exception during transactional operation  at -------------> " + pjp , ex);
					try {
						if (entityTransaction.isActive()) {
							entityTransaction.rollback();
							log.debug("transaction rolled back ..");
						}
					} catch (Throwable t) {
						log.debug("unable to rollback!!!", t);
					}
				} else{
					log.debug(" entityTransaction was null, so could not call rollback on entityTransaction ",ex);
				}
				if ( entityManager!= null) {
					entityManagerUtil.closeEntityManager();
				}
			} catch (Throwable rbEx) {
				log.debug("Could not rollback transaction after exception!", rbEx);
			}
			// Let others handle it
			throw ex;
		} finally {
			if (!isActive) {
				entityManagerUtil.closeEntityManager();
			}
		}
	}
}
