package com.gramercysoftware.persistence.transaction.annotation;

/**
 * Any method with this annotation will be executed under a db transaction.
 * Nesting is supported i.e a method with this annotation can call another one
 * with the same annotation in which case the top level method will be the one
 * controlling transactional boundary.
 */
public @interface Transactional {
}
