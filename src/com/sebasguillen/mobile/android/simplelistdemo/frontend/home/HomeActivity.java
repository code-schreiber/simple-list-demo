package com.sebasguillen.mobile.android.simplelistdemo.frontend.home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.sebasguillen.mobile.android.simplelistdemo.R;
import com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO;
import com.sebasguillen.mobile.android.simplelistdemo.frontend.MyPopup;
import com.sebasguillen.mobile.android.simplelistdemo.frontend.TasksAdapter;

/**
 * Main activity which shows the list and its elements
 * @author Sebastian Guillen
 */
public class HomeActivity extends Activity{

	private static final String TAG = HomeActivity.class.getSimpleName();

	private AutoCompleteTextView newTaskfield;
	private MenuItem taskCount;

	//Database
	private DAO dao;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home);
		initSearchField();
		initButtons();
		initList();
	}

	@Override
	public void onResume() {
		newTaskfield.setAdapter(getSuggestionsAdapter());
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		exitApp();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// Close the database
		dao.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.menu_home, menu);
		this.taskCount = menu.findItem(R.id.taskCount_option);
		this.taskCount.setEnabled(false);
		updateTasksNumber();
		return super.onCreateOptionsMenu(menu);
	}

	private void initSearchField() {
		newTaskfield = (AutoCompleteTextView) findViewById(R.id.searchfield);
		//Suggest after first character
		newTaskfield.setThreshold(1);
		newTaskfield.setOnEditorActionListener(getOnEditListener());
		newTaskfield.addTextChangedListener(getTextWatcher());
	}

	private void initButtons() {
		Button b = (Button) findViewById(R.id.SearchButton);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tryToAddTask();
			}
		});
		b = (Button) findViewById(R.id.clearSearch_Button);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearTaskField();
				newTaskfield.requestFocus();
				showKeyboard(newTaskfield);
			}
		});

	}

	private void initList(){
		dao = new DAO(this);

		TasksAdapter adapter = new TasksAdapter(this, dao.getcursor(), 0);
		ListView taskList = (ListView) findViewById(R.id.tasks_listview);
		taskList.setAdapter(adapter);

		taskList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				CheckBox checkBox = (CheckBox) ((RelativeLayout)v).getChildAt(0);
				boolean complete;
				if(checkBox.isChecked()){
					complete = false;
				}else{
					complete = true;
				}
				checkBox.setChecked(complete);
				updateTask(id, complete);
			}
		});
		taskList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				showTooltip(v, (int) id);
				return true;
			}
		});
	}


	/**
	 * Note: Better suggestions would make this app nicer.
	 * @return an adapter with all tasks as suggestions
	 */
	private ArrayAdapter<String> getSuggestionsAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		adapter.addAll(dao.getTasksNames());
		return adapter;
	}

	private void showTooltip(final View v, final int id) {
		final MyPopup popup = new MyPopup(v,HomeActivity.this);
		Button b = new Button(this);
		b.setText(getString(R.string.Share));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Share the task
				shareTask(v);
				popup.dismiss();
			}
		});
		popup.addButton(b);
		b = new Button(this);
		b.setText(getString(R.string.erase_task));
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Delete task from the database
				removeTaskFromDB(id);
				popup.dismiss();
			}
		});
		popup.addButton(b);
		popup.showPopup();
	}

	/** Share the task
	 * @param v the tasks view
	 */
	private void shareTask(View v) {
		//Get the text from the view
		String taskText = ((TextView) ((RelativeLayout)v).getChildAt(0)).getText().toString();
		String textToShare = getString(R.string.SharingText) + " \"" + taskText + "\"";
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT, textToShare);
		startActivity(Intent.createChooser(i, getString(R.string.Share)));
	}

	private void updateTasksNumber() {
		Resources res = getResources();
		ListView taskList = (ListView) findViewById(R.id.tasks_listview);
		int nrOfTasks = taskList.getAdapter().getCount();
		String jobsFoundString;
		jobsFoundString = res.getQuantityString(R.plurals.numberOfTasks, nrOfTasks, nrOfTasks);
		this.taskCount.setTitle(jobsFoundString);
	}

	private OnEditorActionListener getOnEditListener() {
		return new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					//NOTE: not all keyboards support this
					tryToAddTask();
					return true;
				}
				//That's why we also do this
				if (actionId == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					tryToAddTask();
					return true;
				}
				return false;
			}
		};
	}

	private TextWatcher getTextWatcher() {
		final Button clearButton = ((Button) findViewById(R.id.clearSearch_Button));
		final Button saveButton = ((Button) findViewById(R.id.SearchButton));
		clearButton.setVisibility(View.GONE);
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() == 0) {
					clearButton.setVisibility(View.GONE);
					saveButton.setEnabled(false);
				} else {
					clearButton.setVisibility(View.VISIBLE);
					saveButton.setEnabled(true);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				// Not needed
			}
			@Override
			public void afterTextChanged(Editable s) {
				// Not needed
			}
		};
	}

	private String getNewTaskText() {
		return newTaskfield.getText().toString().trim();
	}

	private void tryToAddTask() {
		String text = getNewTaskText();
		//min 1 characters
		if(text.isEmpty()){
			Toast t = Toast.makeText(HomeActivity.this, getString(R.string.noTextWarning), Toast.LENGTH_SHORT);
			//We change the toast's position so it doesn't get over the keyboard, where it is hard to see
			t.setGravity(Gravity.TOP, 0, newTaskfield.getHeight());
			t.show();
		}
		else{
			addNewTask(text);
		}
	}

	private void addNewTask(String task) {
		hideKeyboard();
		clearTaskField();
		addTaskToDB(task);
		refreshList();
		updateTasksNumber();
	}

	private void clearTaskField() {
		newTaskfield.setText("");
	}

	/**
	 * Persists the new task
	 * @param task the task
	 */
	private void addTaskToDB(String task) {
		dao.createTask(task);
		refreshList();
		updateTasksNumber();
	}

	/**
	 * Persists the new task
	 * @param task the task
	 */
	private void removeTaskFromDB(int taskID) {
		dao.deleteTask(taskID);
		refreshList();
		updateTasksNumber();
	}

	/**
	 * Marks the view as completed or not
	 */
	private void updateTask(float taskId, boolean complete) {
		dao.updateTask((int) taskId, complete);
		refreshList();
	}

	/**
	 * Takes care that the shown list has the same elements as the db
	 */
	private void refreshList() {
		ListView list = (ListView) findViewById(R.id.tasks_listview);
		((TasksAdapter) list.getAdapter()).changeCursor(dao.getcursor());
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	private void showKeyboard(TextView tv) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(tv, InputMethodManager.SHOW_IMPLICIT);
	}

	private void exitApp(){
		this.finish();
	}

}