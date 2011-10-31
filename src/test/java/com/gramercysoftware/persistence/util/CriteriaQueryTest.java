package com.gramercysoftware.persistence.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;

import com.gramercysoftware.persistence.DatabaseTestCase;
import com.gramercysoftware.persistence.DummyEntity;

public class CriteriaQueryTest extends DatabaseTestCase {
	@Test
	public void test() throws Exception {
		CriteriaBuilder b = DefaultEntityManagerUtil.getInstance().getEntityManager().getCriteriaBuilder();
		CriteriaQuery<DummyEntity> q = b.createQuery(DummyEntity.class);
	}
}
