package com.sebasguillen.mobile.android.simplelistdemo.frontend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sebasguillen.mobile.android.simplelistdemo.R;
import com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO;
import com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task;

/**
 * View Holder Pattern: This is approximate 15 % faster than using findViewById().
 * This class initiates the different methods every row in the
 * list uses when interacting with the user.
 * 
 * @author Sebastian Guillen
 */
class RowViewHolder {

	protected CheckBox checkBox;
	protected TextView textView;
	protected Button editText;
	protected boolean expanded;
	protected int id;
	private TasksAdapter adapter;

	protected RowViewHolder(CheckBox cb, TextView tv, Button b, boolean e, TasksAdapter ad) {
		this.checkBox = cb;
		this.textView = tv;
		this.editText = b;
		this.expanded = e;
		//id gets its value on bindView() in Tasksadapter
		this.adapter = ad;

		this.checkBox.setOnClickListener(getCheckBoxListener());
		this.textView.setOnClickListener(getTextViewListener());
		this.textView.setOnLongClickListener(getTextViewLongClickListener());
		this.editText.setOnClickListener(getEditTextListener());
	}

	private OnClickListener getCheckBoxListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTask(v, RowViewHolder.this.id);
			}
		};
	}

	private OnClickListener getTextViewListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				resizeView(RowViewHolder.this);
			}
		};
	}

	private OnLongClickListener getTextViewLongClickListener() {
		return new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showTooltip(v);
				return true;
			}
		};
	}

	private OnClickListener getEditTextListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				showEditDialog(v, id);
			}
		};
	}

	/**
	 * Expands or contracts the task's view
	 * Toggles single line and edit button
	 * @param holder the view's holder
	 */
	private void resizeView(RowViewHolder holder) {
		RowViewHolder.this.expanded = !RowViewHolder.this.expanded;
		holder.textView.setSingleLine(!holder.expanded);
		// Show or hide edit task button's visibility
		if (holder.expanded) {
			holder.editText.setVisibility(View.VISIBLE);
		} else {
			holder.editText.setVisibility(View.GONE);
		}
	}

	private void showTooltip(final View v) {
		final MyPopup popup = new MyPopup(v, (Activity) v.getContext());
		Button b = new Button(v.getContext());
		b.setText(v.getContext().getString(R.string.Edit));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Edit the task
				showEditDialog(view, RowViewHolder.this.id);
				popup.dismiss();
			}
		});
		popup.addButton(b);
		b = new Button(v.getContext());
		b.setText(v.getContext().getString(R.string.Share));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Share the task
				shareTask(view, RowViewHolder.this.id);
				popup.dismiss();
			}
		});
		popup.addButton(b);
		b = new Button(v.getContext());
		b.setText(v.getContext().getString(R.string.erase_task));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Delete task from the database
				removeTask(view, RowViewHolder.this.id);
				popup.dismiss();
			}
		});
		popup.addButton(b);
		popup.showPopup();
	}

	private void showEditDialog(final View v, final int taskId) {
		Context c = v.getContext();
		final EditText input = new EditText(c);
		input.setText(new DAO(v.getContext()).getTask(taskId).getText());
		new AlertDialog.Builder(c)
		.setTitle(c.getString(R.string.Edit))
		.setView(input)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String newText = input.getText().toString();
				editTask(v, taskId, newText);
			}
		}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do nothing.
			}
		}).show();
	}

	/**
	 * Persists the new task
	 * @param task the task
	 */
	private void removeTask(View v, int taskID) {
		DAO dao = new DAO(v.getContext());
		dao.deleteTask(taskID);
		this.adapter.changeCursor(dao.getcursor());
		dao.close();
	}

	/** Edit the task replacing the old text */
	private void editTask(View v, int taskId, String newText) {
		DAO dao = new DAO(v.getContext());
		dao.updateTask(taskId, newText);
		this.adapter.changeCursor(dao.getcursor());
		dao.close();
	}

	/**
	 * Marks the task as completed or not
	 */
	private void updateTask(View v, int taskId) {
		DAO dao = new DAO(v.getContext());
		Task task = dao.getTask(taskId);
		//Revert completed state
		dao.updateTask(taskId, !task.getCompleted());
		this.adapter.changeCursor(dao.getcursor());
		dao.close();
	}

	/** Share the task
	 * @param taskId the task's id
	 */
	private void shareTask(View v, int taskId) {
		//Get the text from the task
		DAO dao = new DAO(v.getContext());
		String taskText = dao.getTask(taskId).getText();
		dao.close();
		String textToShare = v.getContext().getString(R.string.SharingText) + " \"" + taskText + "\"";
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT, textToShare);
		v.getContext().startActivity(Intent.createChooser(i, v.getContext().getString(R.string.Share)));
	}

}