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
	// Tasks are not marked as completed at moment of creation
	private static final String STANDARD_COMPLETED_STATE = "false";
	private static final String EQUALS = " = ";

	private static final String[] ALL_TABLE_COLUMNS = new String[] {
		SQLiteHelper._ID,
		SQLiteHelper.TASK_COLUMN,
		SQLiteHelper.COMPLETED_COLUMN };

	private static final int ID_INDEX = 0;
	private static final int TASK_COLUMN_INDEX = 1;
	private static final int COMPLETED_COLUMN_INDEX = 2;

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
		values.put(SQLiteHelper.COMPLETED_COLUMN, STANDARD_COMPLETED_STATE);
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
	 * Get the task from db where the id match
	 * @param id the id of the task
	 */
	public Task getTask(int id) {
		String whereClause = SQLiteHelper._ID + EQUALS + id;
		Cursor dbTask = db.query(SQLiteHelper.TABLE_NAME, ALL_TABLE_COLUMNS, whereClause, 	null, null, null, null);
		dbTask.moveToFirst();
		Task task = new Task();
		task.setId(dbTask.getInt(ID_INDEX));
		task.setText(dbTask.getString(TASK_COLUMN_INDEX));
		task.setCompleted(Boolean.valueOf(dbTask.getString(COMPLETED_COLUMN_INDEX)));
		dbTask.close();
		return task;
	}

	/**
	 * Marks a task as complete or incomplete in the db
	 * @param taskId the id of the task
	 * @param complete
	 */
	public void updateTask(int taskId, boolean complete) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COMPLETED_COLUMN, Boolean.toString(complete));
		updateTask(taskId, values);
	}

	/**
	 * Changes the text of the give task
	 * @param id the id of the task
	 * @param newText the text to put in
	 */
	public void updateTask(int taskId, String newText) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.TASK_COLUMN, newText);
		updateTask(taskId, values);
	}

	private void updateTask(int taskId, ContentValues values) {
		String whereClause = SQLiteHelper._ID + EQUALS + taskId;
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
			// Take value from the DB
			task.setText(cursor.getString(TASK_COLUMN_INDEX));
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
