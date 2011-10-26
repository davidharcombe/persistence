package com.gramercysoftware.persistence.util;

import com.gramercysoftware.persistence.dao.GenericJpaDao;

public class FooDaoImpl extends GenericJpaDao<Foo, Long> implements FooDao {
	public FooDaoImpl() {
		super(Foo.class);
	}
}