package com.sogou.mobiletoolassist.service;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.AssistApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class ToolsNotifBelowIceCreamSandwich extends Service {
	private int notifiid = 999;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		GenerateNotification();
		return Service.START_STICKY;
	}

	private void GenerateNotification() {
		Context context = AssistApplication.getContext();
		if (context == null)
			return;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		//Notification notif = new Notification(R.drawable.ic_launcher,
		//		"Hello,there!", System.currentTimeMillis());
		// notif.icon = R.drawable.ic_launcher;

		//CharSequence contentTitle = "My notification";
		//CharSequence contentText = "Hello World";
		
		Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);   
		notificationIntent.setClass(context, AssistActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
				Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);  
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		//notif.setLatestEventInfo(context, contentTitle, contentText,
			//	contentIntent);
		Builder builder = new NotificationCompat.Builder(context);
		builder.setContentIntent(contentIntent).setAutoCancel(false)
			.setSmallIcon(R.drawable.ic_launcher).setOngoing(true)
			.setContentTitle("点击进入测试助手")
			.setContentText("zs");
		nm.notify(notifiid, builder.build());
	}
}
