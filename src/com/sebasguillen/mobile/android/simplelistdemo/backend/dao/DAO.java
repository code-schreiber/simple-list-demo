package com.sebasguillen.mobile.android.simplelistdemo.backend.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task;
import com.sebasguillen.mobile.android.simplelistdemo.backend.sql.SQLiteHelper;

/**
 * The Data Access Object.
 * Respecting the single responsibility principle.
 * @author Sebastian Guillen
 */
public class DAO {

	private static final String TAG = DAO.class.getSimpleName();

	private static final String EQUALS = " = ";

	private static final String[] ALL_TABLE_COLUMNS = new String[] {
		SQLiteHelper._ID, SQLiteHelper.TASK_COLUMN,
		SQLiteHelper.COMPLETED_COLUMN };

	private SQLiteDatabase db;
	private SQLiteHelper helper;

	public DAO(Context c) {
		helper = new SQLiteHelper(c);
		db = helper.getWritableDatabase();
	}

	// Close the database
	public void close() {
		db.close();
	}

	/**
	 * Create new task element in db
	 * @param taskText
	 */
	public void createTask(String taskText) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.TASK_COLUMN, taskText);
		values.put(SQLiteHelper.COMPLETED_COLUMN, "false");
		// Insert into DB
		db.insert(SQLiteHelper.TABLE_NAME, null, values);
	}

	/**
	 * Delete entry from db where id's match
	 * @param id the id of the task
	 */
	public void deleteTask(int id) {
		db.delete(SQLiteHelper.TABLE_NAME, SQLiteHelper._ID + EQUALS + id, null);
	}

	/**
	 * Marks a task as complete or incomplete in the db
	 * @param id the id of the task
	 * @param complete
	 */
	public void updateTask(int id, boolean complete) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COMPLETED_COLUMN, Boolean.toString(complete));
		String whereClause = SQLiteHelper._ID + EQUALS + id;
		db.update(SQLiteHelper.TABLE_NAME, values, whereClause , null);
	}

	/**
	 * @return all task names in the database
	 */
	public List<String> getTasksNames() {
		List<String> tasksList = new ArrayList<String>();

		// Query the database
		Cursor cursor = getcursor();
		cursor.moveToFirst();

		// Iterate the results
		while (!cursor.isAfterLast()) {
			Task task = new Task();
			// Take values from the DB
			task.setId(cursor.getInt(0));
			task.setText(cursor.getString(1));

			// Add to the list
			tasksList.add(task.getText());

			// Move to the next result
			cursor.moveToNext();
		}
		cursor.close();
		return tasksList;
	}

	/**
	 * @return the cursor (last added tasks first, completed tasks last)
	 */
	public Cursor getcursor() {
		String orderBy = SQLiteHelper.COMPLETED_COLUMN+","+SQLiteHelper._ID + " DESC";
		Cursor cursor = db.query(SQLiteHelper.TABLE_NAME, ALL_TABLE_COLUMNS, null, null, null, null, orderBy);
		return cursor;
	}

}
