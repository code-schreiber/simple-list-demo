package com.sebasguillen.mobile.android.simplelistdemo.frontend.home;

import android.app.Activity;
import android.content.res.Resources;
import android.database.DataSetObserver;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.sebasguillen.mobile.android.simplelistdemo.R;
import com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO;
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
		newTaskfield = (AutoCompleteTextView) findViewById(R.id.addTask_AutoCompleteTextView);
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
		b = (Button) findViewById(R.id.clearText_Button);
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
		final ListView taskList = (ListView) findViewById(R.id.tasks_listview);
		taskList.setAdapter(adapter);
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				updateTasksNumber();
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

	private void updateTasksNumber() {
		Resources res = getResources();
		ListView taskList = (ListView) findViewById(R.id.tasks_listview);
		int nrOfTasks = taskList.getAdapter().getCount();
		String jobsFoundString;
		jobsFoundString = res.getQuantityString(R.plurals.numberOfTasks, nrOfTasks, nrOfTasks);
		this.taskCount.setTitle(jobsFoundString);
		toggleHelpView(nrOfTasks);
	}

	/**
	 * Changes the list view to a help view when there are no tasks.
	 * For example on first time use or when having deleted all tasks
	 * @param nrOfTasks how many tasks there are.
	 */
	private void toggleHelpView(int nrOfTasks) {
		int firstTimeVisibility = View.GONE;
		int listViewVisibility = View.VISIBLE;
		if(nrOfTasks == 0){
			firstTimeVisibility = View.VISIBLE;
			listViewVisibility = View.GONE;
		}
		((LinearLayout)findViewById(R.id.tasks_listview).getParent()).setVisibility(listViewVisibility);
		findViewById(R.id.firstTimeUse_LinearLayout).setVisibility(firstTimeVisibility);
	}

	private OnEditorActionListener getOnEditListener() {
		return new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
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
		final Button clearButton = ((Button) findViewById(R.id.clearText_Button));
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
		//min 1 character
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
	}

	private void clearTaskField() {
		newTaskfield.setText("");
	}

	/**
	 * Persists the new task
	 * @param taskText the task's text
	 */
	private void addTaskToDB(String taskText) {
		dao.createTask(taskText);
		refreshList();
	}

	/**
	 * Takes care that the shown list has the same elements as the db.
	 * This also triggers updateTasksNumber()
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