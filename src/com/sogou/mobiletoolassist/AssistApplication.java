package com.sogou.mobiletoolassist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AssistApplication extends Application {
	private static Context ctx = null;

	@Override
	public void onCreate() {
		super.onCreate();
		ctx = getApplicationContext();
	}

	public static Context getContext() {
		return ctx;
	}

	public static String getVersion() {
		Context context = getContext();
		String packageName = context.getPackageName();
		try {
			PackageManager pm = context.getPackageManager();
			return pm.getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("FTPSERVER", "Unable to find the name " + packageName
					+ " in the package");
			return null;
		}
	}

	public static SharedPreferences getAppDataPreferences() {
		Context context = getContext();
		return context.getSharedPreferences(
				context.getString(R.string.cfg_appdata), MODE_PRIVATE);
	}

	public static String getAppDataString(String name) {
		SharedPreferences spPreferences = getAppDataPreferences();
		return spPreferences.getString(name, "");
	}

	public static Long getAppDataLong(String name) {
		SharedPreferences spPreferences = getAppDataPreferences();
		return spPreferences.getLong(name, -1);
	}

	public static boolean putString(String name, String value) {
		SharedPreferences sharedPreferences = getAppDataPreferences();
		return sharedPreferences.edit().putString(name, value).commit();
	}

	public static boolean putEmailAddr(String value) {
		SharedPreferences sharedPreferences = getAppDataPreferences();
		return sharedPreferences
				.edit()
				.putString(getContext().getString(R.string.cfg_key_recevier),
						value).commit();
	}

	public static boolean putEmailName(String value) {
		SharedPreferences sharedPreferences = getAppDataPreferences();
		return sharedPreferences.edit().putString("name", value).commit();
	}

	public static boolean putIp(String ip) {
		SharedPreferences sharedPreferences = getAppDataPreferences();
		return sharedPreferences.edit().putString("proxyHost", ip).commit();

	}
	
	public static boolean putPort(String port) {
		SharedPreferences sharedPreferences = getAppDataPreferences();
		return sharedPreferences.edit().putString("proxyPort", port).commit();

	}
}
