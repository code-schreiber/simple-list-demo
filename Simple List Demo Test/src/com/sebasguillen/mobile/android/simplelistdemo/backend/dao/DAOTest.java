package com.sebasguillen.mobile.android.simplelistdemo.backend.dao;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

/**
 * Test class for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO}.
 * @author Sebastian Guillen
 */
public class DAOTest extends AndroidTestCase {

	private Context context;
	private DAO dao;
	private static final String TASKTEXT = "taskText";

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
		dao = new DAO(context);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		cleanDatabase();
		dao.close();
		super.tearDown();
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO#DAO(android.content.Context)}.
	 */
	public void testDAO() {
		assertNotNull(dao);
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO#close()}.
	 */
	public void testClose() {
		dao.close();
		try {
			dao.deleteTask(0);
		} catch (IllegalStateException e) {
			// This should happen
			// This opens dao again for tearDown()
			dao = new DAO(context);
			return;
		}
		fail();
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO#createTask(java.lang.String)}.
	 */
	public void testCreateTask() {
		assertEquals(0, dao.getcursor().getCount());
		dao.createTask(TASKTEXT);
		List<String> names = dao.getTasksNames();
		assertEquals(1, dao.getcursor().getCount());
		assertEquals(1, names.size());
		assertTrue(names.contains(TASKTEXT));
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO#deleteTask(int)}.
	 */
	public void testDeleteTask() {
		assertEquals(0, dao.getcursor().getCount());
		cleanDatabase();
		assertEquals(0, dao.getcursor().getCount());
		dao.createTask(TASKTEXT);
		cleanDatabase();
		assertEquals(0, dao.getcursor().getCount());
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO#updateTask(int, boolean)}.
	 */
	public void testUpdateTask() {
		dao.createTask(TASKTEXT);
		Cursor beforeUpdate = dao.getcursor();
		int FIRST_GIVEN_ID = 0;
		dao.updateTask(FIRST_GIVEN_ID , true);
		Cursor afterUpdate = dao.getcursor();
		assertSame(beforeUpdate, beforeUpdate);
		assertNotSame(beforeUpdate, afterUpdate);
		beforeUpdate.close();
		afterUpdate.close();
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO#getTasksNames()}.
	 */
	public void testGetTasksNames() {
		List<String> names = dao.getTasksNames();
		assertTrue(names.isEmpty());
		dao.createTask(TASKTEXT);
		names = dao.getTasksNames();
		assertFalse(names.isEmpty());
		assertTrue(names.contains(TASKTEXT));
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO#getcursor()}.
	 */
	public void testGetcursor() {
		assertNotNull(dao.getcursor());
		dao.createTask(TASKTEXT);
		assertEquals(1, dao.getcursor().getCount());
	}

	/**
	 * Clean all database rows
	 */
	private void cleanDatabase() {
		int id = 0;
		while(!dao.getTasksNames().isEmpty()){
			dao.deleteTask(id++);
		}
	}

}
