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
