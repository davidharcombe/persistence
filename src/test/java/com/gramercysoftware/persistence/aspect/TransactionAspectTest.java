package com.gramercysoftware.persistence.aspect;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.aspectj.lang.Aspects;
import org.junit.Before;
import org.junit.Test;

import com.gramercysoftware.persistence.TestEntity;
import com.gramercysoftware.persistence.TestEntity.Type;
import com.gramercysoftware.persistence.access.DatabaseTestCase;
import com.gramercysoftware.persistence.dao.GenericJpaDao;
import com.gramercysoftware.persistence.transaction.annotation.Transactional;
import com.gramercysoftware.persistence.transactions.AnnotationDrivenTransactionalAspect;
import com.gramercysoftware.persistence.util.DefaultEntityManagerUtil;

/**
 * 
 * @author vkalyanasundaram
 * 
 */
public class TransactionAspectTest extends DatabaseTestCase {

	@Before
	public void setUpData() throws Exception {
		super.setUpData();
		Aspects.aspectOf(AnnotationDrivenTransactionalAspect.class);
	}

	@Test
	public void testSimpleTransaction() {
		Service testService = new Service();
		testService.insertOneRowInTransaction();
		GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
			DefaultEntityManagerUtil.getInstance());

		List<TestEntity> serviceUrlList = genericJpaDao.getAll();
		assertEquals(1, serviceUrlList.size());

	}

	@Test
	public void testNestedTransaction() {
		Service testService = new Service();
		testService.insertRowsFromNestedTransaction();
		GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
			DefaultEntityManagerUtil.getInstance());

		List<TestEntity> serviceUrlList = genericJpaDao.getAll();
		assertEquals(2, serviceUrlList.size());
	}

	@Test
	public void testNestedTransactionWithChildTransactionThrowingExcepion() {
		Service testService = new Service();
		try {
			testService.insertRowsFromNestedTransactionWithChildTransactionThrowingException();
		} catch (Exception e) {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			List<TestEntity> serviceUrlList = genericJpaDao.getAll();
			assertEquals(0, serviceUrlList.size());
		}
	}

	@Test
	public void testInsertRowButMarkATransactionForRollback() {
		Service testService = new Service();

		testService.insertRowButMarkATransactionForRollback();

		GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
			DefaultEntityManagerUtil.getInstance());
		List<TestEntity> serviceUrlList = genericJpaDao.getAll();
		assertEquals(0, serviceUrlList.size());

	}

	@Test(expected = IllegalStateException.class)
	public void testInsertRowButRollbackTheChildTransaction() {
		Service testService = new Service();

		testService.insertRowButRollbackTheChildTransaction();

	}

	@Test
	public void testSaveDataWithoutTransaction() {
		Service testService = new Service();
		testService.insertOneRowInTransaction();
		testService.saveDataWithoutTransaction();
		testService.insertOneRowInTransaction();
		GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
			DefaultEntityManagerUtil.getInstance());
		List<TestEntity> serviceUrlList = genericJpaDao.getAll();
		assertEquals(2, serviceUrlList.size());
	}

	private static class Service {

		@Transactional
		public void insertOneRowInTransaction() {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.FOO, "hi", false));
		}

		@Transactional
		public void insertRowsFromNestedTransaction() {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.BAR, "bye", true));
			insertOneRowInTransaction();
		}

		@Transactional
		public void insertRowsFromNestedTransactionWithChildTransactionThrowingException() {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.BAR, "bye", true));
			throwExceptionFromATransaction();
		}

		@Transactional
		public void insertRowButMarkATransactionForRollback() {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.BAR, "bye", true));
			genericJpaDao.getEntityManagerUtil().getEntityManager().getTransaction().setRollbackOnly();
		}

		@Transactional
		public void insertRowButRollbackTheChildTransaction() {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.BAR, "bye", true));
			rollbackChild();

		}

		@Transactional
		public void throwExceptionFromATransaction() {
			throw new RuntimeException("Some exception ..");
		}

		@Transactional
		public void rollbackChild() {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.getEntityManagerUtil().getEntityManager().getTransaction().rollback();
		}

		public void saveDataWithoutTransaction() {
			GenericJpaDao<TestEntity, Integer> genericJpaDao = new GenericJpaDao<TestEntity, Integer>(TestEntity.class,
				DefaultEntityManagerUtil.getInstance());
			TestEntity testEntity = new TestEntity();
			testEntity.setAvailable(true);
			testEntity.setMessage("saveDataWithoutTransaction");
			testEntity.setType(Type.BAR);
			genericJpaDao.getEntityManagerUtil().getEntityManager().persist(testEntity);
		}

		private TestEntity createServiceUrl(Type type, String message, boolean isAvailable) {
			TestEntity testEntity = new TestEntity();
			testEntity.setMessage(message);
			testEntity.setAvailable(isAvailable);
			testEntity.setType(type);
			return testEntity;
		}
	}
}
