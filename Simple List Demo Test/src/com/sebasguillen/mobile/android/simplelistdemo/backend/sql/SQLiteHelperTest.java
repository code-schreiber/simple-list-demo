package com.sebasguillen.mobile.android.simplelistdemo.backend.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Test class for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.sql.SQLiteHelper}.
 * @author Sebastian Guillen
 */
public class SQLiteHelperTest extends AndroidTestCase {

	private Context context;
	private SQLiteHelper helper;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
		helper = new SQLiteHelper(context);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		helper.close();
		super.tearDown();
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.sql.SQLiteHelper#SQLiteHelper(android.content.Context)}.
	 */
	public void testSQLiteHelper() {
		assertNotNull(helper);
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.sql.SQLiteHelper#onCreate(android.database.sqlite.SQLiteDatabase)}.
	 */
	public void testOnCreateSQLiteDatabase() {
		// onCreate() was called with constructor
		// and created the database already
		SQLiteDatabase db = helper.getWritableDatabase();
		assertTrue(db.isOpen());
		db.close();
		assertFalse(db.isOpen());
	}

	/**
	 * Test method for {@link com.sebasguillen.mobile.android.simplelistdemo.backend.sql.SQLiteHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)}.
	 */
	public void testOnUpgradeSQLiteDatabaseIntInt() {
		SQLiteDatabase db = helper.getWritableDatabase();
		insertOneRow(db);
		Cursor allColumns = getAllRows(db);
		assertEquals(1, allColumns.getCount());
		helper.onUpgrade(db, 0, 0);
		allColumns.close();
		allColumns = getAllRows(db);
		//Check if table was dropped
		assertEquals(0, allColumns.getCount());
		allColumns.close();
		db.close();
	}

	private Cursor getAllRows(SQLiteDatabase db) {
		return db.query(SQLiteHelper.TABLE_NAME, null,  null,  null,  null,  null, null);
	}

	private void insertOneRow(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.TASK_COLUMN, "taskText");
		values.put(SQLiteHelper.COMPLETED_COLUMN, "false");
		db.insert(SQLiteHelper.TABLE_NAME, null, values);
	}

}
