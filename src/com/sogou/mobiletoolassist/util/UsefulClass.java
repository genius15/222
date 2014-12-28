package com.sogou.mobiletoolassist.util;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public class UsefulClass {
	//判断某个服务是否在运行
	public static boolean isServiceRunning(Context mContext,String className) {
		
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE); 
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
		if (serviceList!=null && serviceList.isEmpty()) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className)) {
				return true;
			}
		}
		return false;
	}
}
