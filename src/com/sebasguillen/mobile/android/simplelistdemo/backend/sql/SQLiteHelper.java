package com.sebasguillen.mobile.android.simplelistdemo.backend.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates the application's database.
 * @author Sebastian Guillen
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "TASKS_DB";

	public static final String TABLE_NAME = "TasksTable";
	//Columnns
	public static final String TASK_COLUMN = "task";
	public static final String COMPLETED_COLUMN = "completed";
	public static final String _ID = "_id";

	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * Create the table
	 * Columns are: id, task and completed
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Execute create table SQL
		String createTable = "CREATE TABLE ";
		String comma = ", ";
		String column1 = _ID+" INTEGER PRIMARY KEY AUTOINCREMENT";
		String column2 = TASK_COLUMN+" TEXT NOT NULL";
		String column3 = COMPLETED_COLUMN+" TEXT NOT NULL";
		String sql = createTable + TABLE_NAME + " ("+column1+comma+column2+comma+column3+");";
		db.execSQL(sql);
	}

	/**
	 * Recreates the table
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// DROP table
		String drop = "DROP TABLE IF EXISTS ";
		db.execSQL(drop+TABLE_NAME);
		// Recreate table
		onCreate(db);
	}

}
