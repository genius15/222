package com.sogou.mobiletoolassist.setting;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

public class ApkInfoLoader extends AsyncTaskLoader<integer> {
	private List<ApplicationInfo> pkgList = null;
	private Context context = null;
	public ApkInfoLoader(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	public List<ApplicationInfo> getAppInfos(){
		return pkgList;
	}

	@Override
	public integer loadInBackground() {
		// TODO Auto-generated method stub
		
		
		return null;
	}
	
	@Override
	public void onStartLoading(){
		forceLoad();
	}

}
