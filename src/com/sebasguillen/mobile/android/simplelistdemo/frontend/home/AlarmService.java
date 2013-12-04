package com.sebasguillen.mobile.android.simplelistdemo.frontend.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task;
import com.sebasguillen.mobile.android.simplelistdemo.backend.stat.Consts;

public class AlarmService {

	/** Global PendingIntent for this app */
	private PendingIntent alarmSender;
	private Context context;
	private long dueDate;

	public AlarmService(Context context, Task task) {
		this.context = context;
		this.dueDate = task.getDueDate();
		//Task ID as a unique identifier for sender and notification
		int taskID = task.getId();
		Intent intentAlarm = new Intent(context, AlarmReceiver.class);
		intentAlarm.putExtra(Consts.BE_INTENT_TASK_ID_KEY, taskID);
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		alarmSender = PendingIntent.getBroadcast(context, taskID, intentAlarm, flags);
	}

	public void scheduleAlarm(){
		int type = AlarmManager.RTC_WAKEUP;
		long triggerAtMillis = this.dueDate;
		PendingIntent operation = this.alarmSender;
		// Schedule the alarm
		AlarmManager am = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
		am.set(type, triggerAtMillis, operation);
	}

	public static void cancelAlarm(Context c, int taskId) {
		AlarmReceiver.cancelAlarm(c, taskId);
	}


}