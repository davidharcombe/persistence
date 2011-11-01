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
