package com.sogou.mobiletoolassist;

import com.tencent.bugly.crashreport.CrashReport;

import android.R.integer;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AssistApplication extends Application {
	private static Context ctx = null;

	@Override
	public void onCreate() {
		super.onCreate();
		ctx = getApplicationContext();          
	                

		String appId = "900004935"; // 上Bugly(bugly.qq.com)注册产品获取的AppId

		boolean isDebug = false; // true代表App处于调试阶段，false代表App发布阶段

		CrashReport.initCrashReport(ctx, appId, isDebug); // 初始化SDK  

	     
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
	public static String getString(String key) {
		SharedPreferences sharedPreferences = getAppDataPreferences();
		return sharedPreferences.getString(key, null);
	}
	public static boolean putEmailAddrToDefault(String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		return sharedPreferences
				.edit()
				.putString(getContext().getString(R.string.cfg_key_recevier),
						value).commit();
	}

	public static boolean putEmailNameToDefault(String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		return sharedPreferences.edit().putString("name", value).commit();
	}

	public static boolean putIpToDefault(String ip) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		return sharedPreferences.edit().putString("proxyHost", ip).commit();

	}

	public static boolean putPortToDefault(String port) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		return sharedPreferences.edit().putString("proxyPort", port).commit();

	}

	public static String getEmailAddr() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		return sharedPreferences.getString(
				getContext().getString(R.string.cfg_key_recevier),
				null);
	}
	
	public static String getEmailName() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		return sharedPreferences.getString(
				"name",
				"");
	}
	
	public static void ShowToast(String text) {
		Context ctx = getContext();
		Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
	}
	
	public static void ShowToast(String text,int duration) {
		Context ctx = getContext();
		Toast.makeText(ctx, text, duration).show();
	}
}
