package com.sogou.mobiletoolassist.service;

import java.io.File;

import com.sogou.mobiletoolassist.fileobserver.FileObserverThread;
import com.sogou.mobiletoolassist.AssistActivity;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class FileObserverService extends Service {
	private FileObserverThread listener = null;
	private String observerpath = null;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
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
				init(path);//���±������ݿ�·��
				observerpath = path;
				
			}else{
				
			}
		}else{
			
			SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);
			String deafultpath = Environment.getExternalStorageDirectory().getPath();
			deafultpath += File.separator + "MobileTool/CrashReport";
			observerpath = appdata.getString("obPath", deafultpath);
		}
		listener = new FileObserverThread(observerpath);
		//��ʼ����
		
		listener.startWatching();
		
		return Service.START_STICKY;//��ʾ��ϵͳɱ������Ҫ����
	
	}
	
	private void init(String path){
		SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);  
		appdata.edit().putString("obPath", path).commit();  //��ֹ����������path���浽����
		
	}


}