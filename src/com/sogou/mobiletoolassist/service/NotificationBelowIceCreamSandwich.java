package com.sogou.mobiletoolassist.service;



import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.assistApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;

public class NotificationBelowIceCreamSandwich extends Service {
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
	public int onStartCommand(Intent intent, int flags, int startId){
		GenerateNotification();
		
		return Service.START_STICKY;
	}
	@SuppressWarnings("deprecation")
	private void GenerateNotification(){
		Context context = assistApplication.getContext();
		if(context == null)
			return;
		NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        

        //intent.setAction(NotificationUtils.ACTION_STRING);
        //pendingIntent = PendingIntent.getActivity(MobileToolSDK.getAppContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notif = new Notification(R.drawable.ic_launcher, "Hello,there!", System.currentTimeMillis());
        //notif.icon = R.drawable.ic_launcher;
       
        CharSequence contentTitle ="My notification";  
        CharSequence contentText = "Hello World";  
        Intent notificationIntent = new Intent(this,AssistActivity.class);  
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,0 );  
        notif.setLatestEventInfo(context, contentTitle, contentText, contentIntent);  
//        Builder builder = new NotificationCompat.Builder(context);
//        builder.setContentIntent(pendingIntent).setAutoCancel(false);
//        builder.setSmallIcon(R.id.cleardatabtn);
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
//        if (bitmap != null) {
//            builder.setLargeIcon(bitmap);
//        }
//       // builder.setLargeIcon(R.drawable.ic_launcher);
//        builder.setOngoing(true);
        //NotificationUtils.setSmallIcon(builder);
        //NotificationUtils.setLargeIcon(builder);
        //builder.setContentTitle("µã»÷½øÐÐ½ØÍ¼")
         //       .setContentText("zs");
        nm.notify(notifiid, notif);
	}
}
