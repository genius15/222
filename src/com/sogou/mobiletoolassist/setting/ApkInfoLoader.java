package com.sogou.mobiletoolassist.setting;

import java.util.ArrayList;
import java.util.List;

import com.sogou.mobiletoolassist.appmanager.APKUtil;

import android.R.integer;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;

public class ApkInfoLoader extends AsyncTaskLoader<Integer> {
	private List<APKInfo> pkgList = null;
	private Context context = null;
	public ApkInfoLoader(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	public List<APKInfo> getAppInfos(){
		return pkgList;
	}

	@Override
	public Integer loadInBackground() {
		pkgList = APKUtil.getAllUninstalledAPKs();
		
		
		return 0;
	}
	
	@Override
	public void onStartLoading(){
		forceLoad();
	}

}
