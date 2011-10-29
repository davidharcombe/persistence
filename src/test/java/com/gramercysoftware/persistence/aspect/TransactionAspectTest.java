package com.gramercysoftware.persistence.aspect;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.aspectj.lang.Aspects;
import org.junit.Before;
import org.junit.Test;

import com.gramercysoftware.persistence.DummyEntity;
import com.gramercysoftware.persistence.DummyEntity.Type;
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
		GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
			DefaultEntityManagerUtil.getInstance());

		List<DummyEntity> serviceUrlList = genericJpaDao.getAll();
		assertEquals(1, serviceUrlList.size());

	}

	@Test
	public void testNestedTransaction() {
		Service testService = new Service();
		testService.insertRowsFromNestedTransaction();
		GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
			DefaultEntityManagerUtil.getInstance());

		List<DummyEntity> serviceUrlList = genericJpaDao.getAll();
		assertEquals(2, serviceUrlList.size());
	}

	@Test
	public void testNestedTransactionWithChildTransactionThrowingExcepion() {
		Service testService = new Service();
		try {
			testService.insertRowsFromNestedTransactionWithChildTransactionThrowingException();
		} catch (Exception e) {
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
				DefaultEntityManagerUtil.getInstance());
			List<DummyEntity> serviceUrlList = genericJpaDao.getAll();
			assertEquals(0, serviceUrlList.size());
		}
	}

	@Test
	public void testInsertRowButMarkATransactionForRollback() {
		Service testService = new Service();

		testService.insertRowButMarkATransactionForRollback();

		GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
			DefaultEntityManagerUtil.getInstance());
		List<DummyEntity> serviceUrlList = genericJpaDao.getAll();
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
		GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
			DefaultEntityManagerUtil.getInstance());
		List<DummyEntity> serviceUrlList = genericJpaDao.getAll();
		assertEquals(2, serviceUrlList.size());
	}

	private static class Service {

		@Transactional
		public void insertOneRowInTransaction() {
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.FOO, "hi", false));
		}

		@Transactional
		public void insertRowsFromNestedTransaction() {
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.BAR, "bye", true));
			insertOneRowInTransaction();
		}

		@Transactional
		public void insertRowsFromNestedTransactionWithChildTransactionThrowingException() {
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.BAR, "bye", true));
			throwExceptionFromATransaction();
		}

		@Transactional
		public void insertRowButMarkATransactionForRollback() {
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.save(createServiceUrl(Type.BAR, "bye", true));
			genericJpaDao.getEntityManagerUtil().getEntityManager().getTransaction().setRollbackOnly();
		}

		@Transactional
		public void insertRowButRollbackTheChildTransaction() {
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
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
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
				DefaultEntityManagerUtil.getInstance());
			genericJpaDao.getEntityManagerUtil().getEntityManager().getTransaction().rollback();
		}

		public void saveDataWithoutTransaction() {
			GenericJpaDao<DummyEntity, Integer> genericJpaDao = new GenericJpaDao<DummyEntity, Integer>(DummyEntity.class,
				DefaultEntityManagerUtil.getInstance());
			DummyEntity testEntity = new DummyEntity();
			testEntity.setAvailable(true);
			testEntity.setMessage("saveDataWithoutTransaction");
			testEntity.setType(Type.BAR);
			genericJpaDao.getEntityManagerUtil().getEntityManager().persist(testEntity);
		}

		private DummyEntity createServiceUrl(Type type, String message, boolean isAvailable) {
			DummyEntity testEntity = new DummyEntity();
			testEntity.setMessage(message);
			testEntity.setAvailable(isAvailable);
			testEntity.setType(type);
			return testEntity;
		}
	}
}
