package com.gramercysoftware.persistence.util;

import com.gramercysoftware.persistence.access.DatabaseTestCase;

public class DBTestJpaJdbcConnectionJoiner extends DatabaseTestCase {	
//	private static final Long ERROR_CODE = 1000L;
//	private static final long ID_1 = 888L;
//	private static final long ID_2 = 999L;
//	private static final long ID_3 = 777L;
//
//	public DBTestJpaJdbcConnectionJoiner() {
//		skipLoadTestHomestate = true;
//	}	
//
//	@Before
//	public void before() throws Exception {
//		new SQLTestPatch(DBTestJpaJdbcConnectionJoiner.class, 1).apply(DatabaseTestPool.getInstance().getConnection());
//	}
//	
//	@After
//	public void after() throws Exception {
//		new SQLTestPatch(DBTestJpaJdbcConnectionJoiner.class, 2).apply(DatabaseTestPool.getInstance().getConnection());
//	}
//	
//	@Test
//	public void testCommit() throws Exception {
//		EntityManager entityManager = DefaultEntityManagerUtil.getInstance().getEntityManager();
//		entityManager.getTransaction().begin();
//
//		JpaJdbcConnectionJoiner<Object> joiner = new JpaJdbcConnectionJoiner<Object>(entityManager);
//		joiner.doWork(new JdbcWork<Object>() {	
//			@Override
//			public Object execute(Connection connection) throws SQLException {
//				saveDataWithJdbc(connection);
//				return null;
//			}
//		});
//		
//		saveDataWithJpa(ID_1);				
//		entityManager.getTransaction().commit();
//
//		List<Foo> wallets = getFooFromDB();
//		assertEquals(1, wallets.size());
//		assertEquals(ID_1, wallets.get(0).getId());
//
//		List<Long> errors = getErrorCodesFromDB();
//		assertEquals(1, errors.size());
//		assertEquals(ERROR_CODE, errors.get(0));
//	}
//
//	@Test
//	public void testRollback() throws Exception {
//		EntityManager entityManager = DefaultEntityManagerUtil.getInstance().getEntityManager();
//		entityManager.getTransaction().begin();
//
//		JpaJdbcConnectionJoiner<Object> joiner = new JpaJdbcConnectionJoiner<Object>(entityManager);
//		joiner.doWork(new JdbcWork<Object>() {	
//			@Override
//			public Object execute(Connection connection) throws SQLException {
//				saveDataWithJdbc(connection);
//				return null;
//			}
//		});
//		
//		saveDataWithJpa(ID_2);				
//
//		entityManager.getTransaction().rollback();
//		List<Foo> wallets = getFooFromDB();
//		assertTrue(wallets == null || wallets.isEmpty());
//
//		List<Long> errors = getErrorCodesFromDB();
//		assertTrue(errors == null || errors.isEmpty());
//	
//	}
//	
//	@Test
//	public void testDifferentConnectionOnlyJpaIsRolledBack() throws Exception {
//		//This is an example of how we should NOT retrieve the connection from JPA to use it with JDBC.
//		//It will be a different connection than the one used with JPA and rollback will work only with JPA and not JDBC. 
//		EntityManager entityManager = DefaultEntityManagerUtil.getInstance().getEntityManager();
//		entityManager.getTransaction().begin();
//
//		Session session = ((HibernateEntityManager)((GSPEntityManager)entityManager).getWrappedEntityManager()).getSession();
//		
//		SessionFactory sessionFactory = session.getSessionFactory() ;
//		Connection connection = ((SessionFactoryImpl)sessionFactory).getSettings().getConnectionProvider().getConnection();			
//
//		saveDataWithJdbc(connection);
//		
//		saveDataWithJpa(ID_3);				
//		entityManager.getTransaction().rollback();
//
//		List<Foo> wallets = getFooFromDB();
//		assertTrue(wallets == null || wallets.isEmpty());
//
//		List<Long> errors = getErrorCodesFromDB();
//		assertEquals(1, errors.size());
//		assertEquals(ERROR_CODE, errors.get(0));
//	}
//	
//	private List<Foo> getFooFromDB() {
//		FooDao walletDao = new FooDaoImpl();
//		return walletDao.getAll();
//		
//	}
//
//	private List<Long> getErrorCodesFromDB() throws SQLException {
//		Connection connection = DatabasePool.getInstance().getConnection();
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		List<Long> errors = new ArrayList<Long>();
//		
//		try {
//		    ps = connection.prepareStatement("SELECT * from hyperwallet_errors");
//		    rs = ps.executeQuery();
//		    
//		    while (rs.next()) {
//		    	errors.add(rs.getLong("errorcode"));
//		    }
//		    
//		} finally {
//		    if (rs != null) try { rs.close(); } catch (Exception ex) {}
//		    if (ps != null) try { ps.close(); } catch (Exception ex) {}
//	        if (connection != null) { try { connection.close(); } catch (Exception ex) {} }		    
//		}
//		return errors;
//		
//	}
//
//	private void saveDataWithJpa(long id) {
//		FooDao dao = new FooDaoImpl();
//		Foo foo = new Foo();
//		foo.setId(id);
//		foo.setBar(Bar.BAR);
//		foo.setBaz("baz");
//		dao.save(foo);
//		
//	}
//	
//	private void saveDataWithJdbc(Connection connection) throws SQLException {
//		String insertStatement = "INSERT INTO hyperwallet_errors (errorcode, category) VALUES (?,?)";		
//		PreparedStatement ps = null;
//
//		try {
//		    ps = connection.prepareStatement(insertStatement);
//		    ps.setLong(1, ERROR_CODE);
//		    
//	    	ps.setString(2, "error");
//		    ps.executeUpdate();
//	    	
//		} finally {
//		     if (ps != null) try { ps.close(); } catch (Exception e) {}
//		}
//	}
}
