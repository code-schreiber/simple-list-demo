package com.sebasguillen.mobile.android.simplelistdemo.backend.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates and updates the application's database.
 * @author Sebastian Guillen
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "TASKS_DB";

	public static final String TABLE_NAME = "TasksTable";
	//Columnns
	public static final String TASK_COLUMN = "task";
	public static final String COMPLETED_COLUMN = "completed";
	public static final String DATE_COLUMN = "date";
	public static final String _ID = "_id";


	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * Create the table
	 * Columns are: id, task, completed and date
	 * NOTE: When adding columns, onUpgrade should be used to alter the table
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Execute create table SQL
		String createTable = "CREATE TABLE ";
		String comma = ", ";
		String column1 = _ID				+ " INTEGER PRIMARY KEY AUTOINCREMENT";
		String column2 = TASK_COLUMN		+ " TEXT NOT NULL";
		String column3 = COMPLETED_COLUMN	+ " TEXT NOT NULL";
		String column4 = DATE_COLUMN		+ " INTEGER";
		String sql = createTable + TABLE_NAME + " ("+column1+comma+column2+comma+column3+comma+column4+");";
		db.execSQL(sql);
	}

	/**
	 * Recreates the table
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		/*String upgradeQuery = "ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+DATE_COLUMN+" INTEGER;";
		if ( oldVer == 1 && newVer == 2 ) {
			db.execSQL(upgradeQuery);

		}*/
		// DROP table
		//String drop = "DROP TABLE IF EXISTS ";
		//db.execSQL(drop + TABLE_NAME);
		// Recreate table
		//onCreate(db);
	}

}
