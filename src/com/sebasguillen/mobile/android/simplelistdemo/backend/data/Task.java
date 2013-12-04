package com.sebasguillen.mobile.android.simplelistdemo.backend.data;


/**
 * Getters and setters for the task object
 * @author Sebastian Guillen
 */
public class Task {

	private int id;
	private String text;
	private boolean completed;
	private long dueDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long date) {
		this.dueDate = date;
	}

}
