package com.sebasguillen.mobile.android.simplelistdemo.frontend.home;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;

import com.sebasguillen.mobile.android.simplelistdemo.backend.dao.DAO;
import com.sebasguillen.mobile.android.simplelistdemo.backend.data.Task;
import com.sebasguillen.mobile.android.simplelistdemo.backend.stat.Consts;

public class AlarmReceiver extends BroadcastReceiver {
	private int taskId;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.taskId = intent.getExtras().getInt(Consts.BE_INTENT_TASK_ID_KEY);
		if(intent.hasExtra(Consts.BE_INTENT_MARK_TASK_AS_COMPLETE)){
			markAsComplete(context);
		}
		else{
			issueNotification(context);
		}
	}

	private void markAsComplete(Context context) {
		// Mark task as complete
		new DAO(context).updateTask(this.taskId, true);
		//Cancel by hand since else notification stays
		cancelAlarm(context, this.taskId);
	}

	private void issueNotification(Context c) {
		Task task = new DAO(c).getTask(this.taskId);
		String contentTitle = task.getText();
		String formatedDate = DateFormat.getDateFormat(c).format(task.getDueDate());
		formatedDate += Consts.SINGLE_SPACE;
		formatedDate += DateFormat.getTimeFormat(c).format(task.getDueDate());

		// Sets an ID for the notification
		int notificationId = this.taskId;
		Notification notification = getNotification(c, contentTitle, formatedDate);
		// Gets an instance of the NotificationManager service
		NotificationManager nm =
				(NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		nm.notify(notificationId, notification);
	}

	public static void cancelAlarm(Context c, int id) {
		((NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(id);
	}

	private Notification getNotification(Context context, String contentTitle, String date) {
		Notification.Builder builder = getStandardNotification(context, contentTitle, date);
		PendingIntent resultPendingIntent = getPendingIntent(context);
		//Set the Notification's Click Behavior
		builder.setContentIntent(resultPendingIntent);
		Notification bigNotif = getBigNotification(context, date, builder);
		return bigNotif;//FIXME what to do when not API 16
	}

	private PendingIntent getPendingIntent(Context c) {
		//Define Action
		Intent resultIntent = new Intent(c, HomeActivity.class);
		return PendingIntent.getActivity(
				c,
				0,
				resultIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private Builder getStandardNotification(Context c, String title, String dueDate) {
		return new Notification.Builder(c)
		.setLargeIcon(BitmapFactory.decodeResource(c.getResources(), android.R.drawable.ic_menu_agenda))
		.setSmallIcon(android.R.drawable.ic_menu_agenda)
		.setContentTitle(title)
		.setContentText(dueDate)
		.setAutoCancel(true);
	}

	@TargetApi(16)
	private Notification getBigNotification(Context c, String dueDate, Notification.Builder builder) {
		PendingIntent intent = getMarkasCompleteIntent(c);
		builder.addAction(android.R.drawable.checkbox_on_background, "Mark as complete", intent);
		return new Notification.BigTextStyle(builder)
		.bigText("Due on " +  dueDate)
		.build();
	}

	private PendingIntent getMarkasCompleteIntent(Context c) {
		Intent intent= new Intent(c, AlarmReceiver.class);
		intent.putExtra(Consts.BE_INTENT_MARK_TASK_AS_COMPLETE, true);
		intent.putExtra(Consts.BE_INTENT_TASK_ID_KEY, this.taskId);
		PendingIntent markAsCompleteIntent = PendingIntent.getBroadcast(c, this.taskId, intent, PendingIntent.FLAG_ONE_SHOT);
		return markAsCompleteIntent;
	}


}