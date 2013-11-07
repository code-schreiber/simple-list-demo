package com.sebasguillen.mobile.android.simplelistdemo.backend.data;

import junit.framework.TestCase;

/**
 * Test class for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task}.
 * @author Sebastian Guillen
 */
public class TaskTest extends TestCase {

	private static final int ID = 1;
	private static final String TEXT = "text";
	private static final boolean COMPLETED = true;

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task#getId()}.
	 */
	public void testGetId() {
		Task task = new Task();
		assertNotSame(ID, task.getId());
		task.setId(ID);
		assertEquals(ID, task.getId());
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task#setId(int)}.
	 */
	public void testSetId() {
		Task task = new Task();
		task.setId(ID);
		assertEquals(ID, task.getId());
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task#getText()}.
	 */
	public void testGetText() {
		Task task = new Task();
		assertNull(task.getText());
		task.setText(TEXT);
		assertEquals(TEXT, task.getText());
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task#setText(java.lang.String)}.
	 */
	public void testSetText() {
		Task task = new Task();
		task.setText(TEXT);
		assertEquals(TEXT, task.getText());
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task#getCompleted()}.
	 */
	public void testGetCompleted() {
		Task task = new Task();
		assertFalse(task.getCompleted());
		task.setCompleted(COMPLETED);
		assertEquals(COMPLETED, task.getCompleted());
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task#setCompleted(boolean))}.
	 */
	public void testSetCompleted() {
		Task task = new Task();
		task.setCompleted(COMPLETED);
		assertEquals(COMPLETED, task.getCompleted());
	}
}
