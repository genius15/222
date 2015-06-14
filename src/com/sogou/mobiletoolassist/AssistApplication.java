package com.sogou.mobiletoolassist;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AssistApplication extends Application {
	private static Context ctx = null;
	@Override
	public void onCreate(){
		super.onCreate();
		ctx = getApplicationContext();
	}
	
	public static Context getContext(){
		return ctx;
	}
	
	public static String getVersion() {
        Context context = getContext();
        String packageName = context.getPackageName();
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e("FTPSERVER", "Unable to find the name " + packageName + " in the package");
            return null;
        }
    }
}
