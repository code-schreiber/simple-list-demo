package com.sebasguillen.mobile.android.simplelistdemo.frontend.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sebasguillen.mobile.android.simplelistdemo.R;
import com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO;
import com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task;
import com.sebasguillen.mobile.android.simplelistdemo.backend.stat.Consts;
import com.sebasguillen.mobile.android.simplelistdemo.frontend.MyPopup;
import com.sebasguillen.mobile.android.simplelistdemo.frontend.home.AlarmService;

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
	protected TextView dateView;
	protected Button editText;
	protected boolean expanded;
	protected int id;
	private TasksAdapter adapter;

	protected RowViewHolder(CheckBox cb, TextView tv, TextView dv, Button b, boolean e, TasksAdapter ad) {
		this.checkBox = cb;
		this.textView = tv;
		this.dateView = dv;
		this.editText = b;
		this.expanded = e;
		//id gets its value on bindView() in TasksAdapter
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
		// Make or remove links
		if (holder.expanded) {
			holder.editText.setVisibility(View.VISIBLE);
			if(holder.dateView.getText().length() != 0){
				holder.dateView.setVisibility(View.VISIBLE);
			}
			Linkify.addLinks(holder.textView, Linkify.ALL);
		} else {
			holder.editText.setVisibility(View.GONE);
			holder.dateView.setVisibility(View.GONE);
			holder.textView.setText(holder.textView.getText().toString());
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
		final Context c = v.getContext();
		final EditText input = new EditText(c);
		Task task = new DAO(v.getContext()).getTask(taskId);
		input.setText(task.getText());
		final DatePicker datePicker = new DatePicker(c);
		final TimePicker timePicker = new TimePicker(c);
		timePicker.setIs24HourView(DateFormat.is24HourFormat(c));
		final Time pickedTime = new Time();
		final CheckBox datePickerToggle = new CheckBox(c);
		datePickerToggle.setText("Set due date");
		datePickerToggle.setChecked(false);
		OnCheckedChangeListener datePickerToggleListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton b, boolean checked) {
				togglePickers(datePicker, timePicker, checked);

			}
		};
		datePickerToggle.setOnCheckedChangeListener(datePickerToggleListener );

		long date = task.getDueDate();
		if ( date == 0){
			togglePickers(datePicker, timePicker, false);
			datePickerToggle.setChecked(false);
		}else{
			togglePickers(datePicker, timePicker, true);
			datePickerToggle.setChecked(true);
		}
		Time initDate = new Time();
		initDate.setToNow();
		datePicker.init(initDate.year, initDate.month, initDate.monthDay, null);
		datePicker.setCalendarViewShown(false);

		LinearLayout layout = new LinearLayout(c);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(input);
		layout.addView(datePickerToggle);
		layout.addView(datePicker);
		layout.addView(timePicker);

		new AlertDialog.Builder(c)
		.setTitle(c.getString(R.string.Edit_task))
		.setView(layout)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String newText = input.getText().toString();
				if (datePickerToggle.isChecked()){
					pickedTime.set(0, timePicker.getCurrentMinute(), timePicker.getCurrentHour(), datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear());
				}else{
					pickedTime.set(0);
				}
				editTask(v, taskId, newText, pickedTime.toMillis(false));
			}
		}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do nothing.
			}
		})
		.show();
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

	/** Edit the task's text and due date if provided (pickedDate 0 means no date was specified).
	 * If a date is provided a notification alarm is scheduled.
	 */
	private void editTask(View v, int taskId, String newText, long pickedDate) {
		DAO dao = new DAO(v.getContext());
		dao.updateTask(taskId, newText);
		dao.updateTasksDate(taskId, pickedDate);
		this.adapter.changeCursor(dao.getcursor());

		if (pickedDate == 0 ){
			deleteScheduledAlarm(v.getContext(), taskId);
		}else{
			scheduleAlarm(v.getContext(), dao.getTask(taskId));
		}
		dao.close();
	}

	/**
	 * Schedules an alarm to notify on the due date
	 * @param context
	 * @param task
	 */
	private void scheduleAlarm(Context c, Task task) {
		new AlarmService(c,task).scheduleAlarm();
	}

	/**
	 * Deletes a scheduled notification if it exists
	 */
	private void deleteScheduledAlarm(Context c, int taskId) {
		AlarmService.cancelAlarm(c, taskId);
	}

	/**
	 * Marks the task as completed or not
	 */
	private void updateTask(View v, int taskId) {
		DAO dao = new DAO(v.getContext());
		Task task = dao.getTask(taskId);
		//Revert completed state
		dao.updateTask(taskId, !task.isCompleted());
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
		String textToShare = v.getContext().getString(R.string.SharingText) + Consts.SINGLE_SPACE + Consts.APOSTROPHE + taskText + Consts.APOSTROPHE;
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType(Consts.PLAIN_TEXT);
		i.putExtra(Intent.EXTRA_TEXT, textToShare);
		v.getContext().startActivity(Intent.createChooser(i, v.getContext().getString(R.string.Share)));
	}

	private void togglePickers(final DatePicker datePicker, final TimePicker timePicker, boolean show) {
		if(show){
			datePicker.setVisibility(View.VISIBLE);
			timePicker.setVisibility(View.VISIBLE);
		}else{
			datePicker.setVisibility(View.GONE);
			timePicker.setVisibility(View.GONE);
		}
	}

}