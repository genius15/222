package com.sogou.mobiletoolassist.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import com.sogou.mobiletoolassist.StreamReader;
import com.sogou.mobiletoolassist.assistApplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

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
	
	public static boolean hasappnamedxxx(Context mContext,String xxx){
		PackageManager pkgmgr = (PackageManager)mContext.getPackageManager();
		if(pkgmgr != null){
			try {
				PackageInfo pkg = pkgmgr.getPackageInfo(xxx,0);
				if(pkg != null){
					return true;
				}
			} catch (NameNotFoundException e) {
				// 
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static String getDeviceInfo(){
		String info = "";
		info += "Device name:";		
		info += Build.MODEL;
		return info;
	}
	
	public static String getZSPkgInfo(){
		String info = "";
		info += "versionName:";
		Context ctx = assistApplication.getContext();
		if(ctx != null){
			PackageManager pm = ctx.getPackageManager();
			try {
				PackageInfo appinfo = pm.getPackageInfo("com.sogou.androidtool", 0);
				info += appinfo.versionName;
				info += "</br>";
				info += "versionCode:";
				info += appinfo.versionCode;
			} catch (NameNotFoundException e) {
				// 
				e.printStackTrace();
			}
		}
		return info;
	}
	public static Process getRootProcess(){
		ProcessBuilder pb = new ProcessBuilder().redirectErrorStream(true)
				.command("su");
		Process p = null;
		try {
			p = pb.start();
		} catch (IOException e1) {
			// 
			e1.printStackTrace();
		}
		return p;
	}
	public static int processCmd(String cmd) {
		int ret = StateValue.success;
		Process p = getRootProcess();
		if(p==null)
			return StateValue.unroot;
		// We must handle the result stream in another Thread first
		StreamReader stdoutReader = new StreamReader(p.getInputStream(),
				"utf-8");
		stdoutReader.start();

		OutputStream out = p.getOutputStream();
		try {
			out.write((cmd + "\n").getBytes("utf-8"));
			out.write(("exit" + "\n").getBytes("utf-8"));
			out.flush();

		} catch (IOException e) {
			ret = StateValue.cmdfailed;
			e.printStackTrace();
			
		} 	
		String result = stdoutReader.getResult();
		Log.i("scresult", result);
		return ret;
	}
}
