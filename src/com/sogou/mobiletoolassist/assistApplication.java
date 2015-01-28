package com.sogou.mobiletoolassist;

import android.app.Application;
import android.content.Context;

public class assistApplication extends Application {
	private static Context ctx = null;
	@Override
	public void onCreate(){
		super.onCreate();
		ctx = getApplicationContext();
	}
	
	public static Context getContext(){
		return ctx;
	}
}
