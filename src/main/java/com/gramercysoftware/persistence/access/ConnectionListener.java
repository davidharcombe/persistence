package com.gramercysoftware.persistence.access;

/**
 *	ConnectionListener.java
 */
public interface ConnectionListener {
    public abstract void connectionCommitted();
    public abstract void connectionRolledback();
    public abstract void connectionClosed();
}
