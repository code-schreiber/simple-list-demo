package com.sebasguillen.mobile.android.simplelistdemo.backend.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO;

/**
 * Creates the application's database.
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "TASKS_DB_test1";

	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**TODO
	 * Create the table
	 * todos
	 * 		_id 	- key
	 * 		todo	- todo text
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Execute create table SQL
		String createTable = "CREATE TABLE ";
		String comma = ", ";
		String column1 = DAO._ID+" INTEGER PRIMARY KEY AUTOINCREMENT";
		String column2 = DAO.TASK_COLUMN+" TEXT NOT NULL";
		String column3 = DAO.COMPLETED_COLUMN+" TEXT NOT NULL";
		String sql = createTable + DAO.TABLE_NAME + " ("+column1+comma+column2+comma+column3+");";
		db.execSQL(sql);
	}

	/**
	 * Recreates the table
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// DROP table
		String drop = "DROP TABLE IF EXISTS ";
		db.execSQL(drop+DAO.TABLE_NAME);
		// Recreate table
		onCreate(db);
	}

}
