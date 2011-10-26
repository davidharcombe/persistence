package com.gramercysoftware.persistence.transactions;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;

@Aspect
public class DefaultTransactionalAspect extends TransactionalAspect {
	public DefaultTransactionalAspect(){
		super(DefaultEntityManagerUtil.getInstance());
	}
	
	@Pointcut ("")
	void transactionalPoint(){
	}
}
