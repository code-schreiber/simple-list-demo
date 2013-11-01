package com.sebasguillen.mobile.android.simplelistdemo.frontend;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;

import com.sebasguillen.mobile.android.simplelistdemo.R;
import com.sebasguillen.mobile.android.simplelistdemo.backend.sql.SQLiteHelper;

/**
 * Adapter for tasks
 * Sets the text and the checkbox
 * @author Sebastian Guillen
 */
public class TasksAdapter extends CursorAdapter {

	private LayoutInflater inflater;

	public TasksAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.task_checkBox);
		if (checkBox != null) {
			String title = c.getString(c.getColumnIndexOrThrow(SQLiteHelper.TASK_COLUMN));
			String checked = c.getString(c.getColumnIndexOrThrow(SQLiteHelper.COMPLETED_COLUMN));
			checkBox.setText(title);
			checkBox.setChecked(Boolean.valueOf(checked));
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.listitem_task, parent, false);
	}

}