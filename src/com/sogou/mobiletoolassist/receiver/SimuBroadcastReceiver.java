package com.sogou.mobiletoolassist.receiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.service.CoreService;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

public class SimuBroadcastReceiver extends BroadcastReceiver {
	public static String broadcastAction = "com.sogou.mobiletoolassist.sendsimu";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean issending = context.getSharedPreferences(
				context.getString(R.string.cfg_appdata), Context.MODE_PRIVATE)
				.getBoolean(context.getString(R.string.issending),false);
		if (!issending) {
			Log.i(AssistActivity.myTag, "stop button is clicked");
			return;
		}
		final String string = intent.getStringExtra("broadcastname");
		final String cmd = "am broadcast -a ";
		UsefulClass.LogToFile(string);
		if (string.equals(Intent.ACTION_PACKAGE_ADDED)) {
			try {
				InputStream in = context.getAssets().open("GT_v2.1.7");
				FileOutputStream out = new FileOutputStream(
						"/sdcard/mttest.apk");

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
				UsefulClass.processCmd("pm install -r /sdcard/mttest.apk");
				Thread.sleep(5000);
				UsefulClass.processCmd("pm uninstall com.tencent.wstt.gt");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		} else if (string.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wifiManager.setWifiEnabled(true);
		} else {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					UsefulClass.processCmd(cmd + string);
					
				}
			}).start();
			
		}
		Intent setAlarmIntent = new Intent(context, CoreService.class);
		setAlarmIntent.putExtra("setalarm", true);
		
		PowerManager.WakeLock wakeLock = null;

		final PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"send test broadcast");
		wakeLock.acquire();
		
			SharedPreferences sPreferences = context.getSharedPreferences(context.getString(R.string.cfg_action_cnt),
					context.MODE_PRIVATE);
			//when sended ,cnt++
			int cnt = sPreferences.getInt(context.getString(R.string.key_action_cnt), 0);
			sPreferences.edit().putInt(context.getString(R.string.key_action_cnt),cnt+1).commit();
			context.startService(setAlarmIntent);
		
		
		wakeLock.release();
		wakeLock = null;
	}

}
