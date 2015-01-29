package com.sogou.mobiletoolassist.service;

import java.io.File;

import com.sogou.mobiletoolassist.fileobserver.FileObserverThread;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;


public class FileObserverService extends Service {
	private FileObserverThread listener = null;
	private String observerpath = null;
	private String emailReceiver = null;
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(intent != null){				
			String path = intent.getStringExtra("observerpath");
			
			if(path != null){
				init(path);//更新本地数据库路径
				observerpath = path;
				
			}else{
				
			}
		}else{
			
			SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);
			String deafultpath = Environment.getExternalStorageDirectory().getPath();
			deafultpath += File.separator + "MobileTool/CrashReport";
			observerpath = appdata.getString("obPath", deafultpath);
		}
		SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);
		emailReceiver = appdata.getString("mailReceiver", "pdatest@sogou-inc.com");
		listener = new FileObserverThread(observerpath,emailReceiver);
		//开始监听
		
		listener.startWatching();
		
		return Service.START_STICKY;//表示被系统杀掉后需要重启
	
	}
	
	private void init(String path){
		SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);  
		appdata.edit().putString("obPath", path).commit();  //防止被重启，把path保存到本地
		
	}


}
