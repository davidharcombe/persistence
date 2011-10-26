package com.gramercysoftware.persistence.transactions;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;

@Aspect
public class AnnotationDrivenTransactionalAspect extends TransactionalAspect {
	
	
	public AnnotationDrivenTransactionalAspect(){
		super(DefaultEntityManagerUtil.getInstance());
	}

	@Pointcut("execution(@com.gramercysoftware.persistence.transaction.annotation.Transactional * *(..)) "
			+ "|| execution(* (@com.gramercysoftware.persistence.transaction.annotation.Transactional *).*(..))")
	void transactionalPoint(){
	}
}
