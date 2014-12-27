package com.sogou.mobiletoolassist.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ClearDataService extends Service {
	private NotificationManager nofitymng = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
