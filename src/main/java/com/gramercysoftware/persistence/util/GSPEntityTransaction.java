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

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

public class GSPEntityTransaction implements EntityTransaction {
	private Logger logger = Logger.getLogger(GSPEntityTransaction.class);
	private final EntityTransaction t;

	public GSPEntityTransaction(EntityTransaction t) {
		this.t = t;
	}

	public void begin() {
		logger.debug("About to begin Transaction!!!");
		t.begin();
		logger.debug("Transaction started");
	}

	public void commit() {
		logger.debug("About to Commit Transaction!!!");
		t.commit();
		logger.debug("Comitted Transaction!!!");
	}

	public boolean getRollbackOnly() {
		return t.getRollbackOnly();
	}

	public boolean isActive() {
		return t.isActive();
	}

	public void rollback() {
		logger.debug("About to Rollback Transaction!!!");
		t.rollback();
		logger.debug("About to Transaction rolled back!!!");
	}

	public void setRollbackOnly() {
		t.setRollbackOnly();
	}
	
}
