package com.sogou.mobiletoolassist.service;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class ToolNotification extends Service implements OnClickListener {

	private final int mNotifitionId = 1;
	public ButtonBroadcastReceiver bReceiver;
	/** 通知栏按钮点击事件对应的ACTION */
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	public final static String INTENT_BUTTONID_TAG = "ButtonId";
	private final IBinder binder = new MyBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	public class MyBinder extends Binder {
		ToolNotification getService() {
			return ToolNotification.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(AssistActivity.myTag, "create clear service");
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		RemoteViews mRemoteViews = new RemoteViews(getPackageName(),
				R.layout.view_custom_button);

		mRemoteViews.setImageViewResource(R.id.custom_song_icon,
				R.drawable.sing_icon);
		// API3.0 以上的时候显示按钮，否则消失
		mRemoteViews.setTextViewText(R.id.notifyTitle, "助手测试工具");
		mRemoteViews.setTextViewText(R.id.notifyContent, "zhangshuai203407");

		// 点击的事件处理
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		buttonIntent.putExtra(INTENT_BUTTONID_TAG, 3);
		PendingIntent intent_next = PendingIntent.getBroadcast(this, 3,
				buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);
		Intent notificationIntent=new Intent(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);   
		notificationIntent.setClass(this, AssistActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
				Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); 
		
		PendingIntent enterAssist = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		mRemoteViews.setOnClickPendingIntent(R.id.toolsNotif, enterAssist);

		NotificationManager mNotifyMgr = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		Log.i("assist", "start command service");
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		builder.setContent(mRemoteViews);
		builder.setOngoing(true);
		Notification nf = builder.build();
		nf.icon = R.drawable.ic_launcher;
		mNotifyMgr.notify(mNotifitionId, nf);

		return Service.START_STICKY;
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// 这里不会被触发
	}

	public class ButtonBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//
			String action = intent.getAction();
			if (action.equals(ACTION_BUTTON)) {
				// 通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case 1:
					// Log.d("assist" , "上一首");
					// Toast.makeText(getApplicationContext(), "上一首",
					// Toast.LENGTH_SHORT).show();
					// break;
				case 2:
					// String play_status = "";
					//
					// //showButtonNotify();
					// Log.d("assist" , play_status);
					// Toast.makeText(getApplicationContext(), play_status,
					// Toast.LENGTH_SHORT).show();
					// break;
				case 3:
					new Thread(new Runnable() {
						@Override
						public void run() {
							Uri uri = RingtoneManager
									.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);// 系统自带提示音
							Ringtone rt = RingtoneManager.getRingtone(
									getApplicationContext(), uri);
							if (rt != null)
								rt.play();
							// TODO 我没用线程时会出现intent接收的android
							// runtime，有时间解一下，先用线程解决
							Looper.prepare();
							CoreService.ScreenShot();
							Looper.loop();

						}

					}).start();

					break;
				default:
					break;
				}
			}
		}
	}

}
