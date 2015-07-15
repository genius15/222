package com.sogou.mobiletoolassist.setting;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.appmanager.APKUtil;
import com.sogou.mobiletoolassit.infostatic.Pingbackhandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ApkInstallConfirmActivity extends Activity {
	private String apkpath = null;
	private String appnameString = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apk_install_confirm);
		Intent intent = getIntent();
		if (intent != null) {
			apkpath = intent.getStringExtra("apkpath");
			appnameString = intent.getStringExtra("appname");
			TextView tView = (TextView) findViewById(R.id.install_confirm_tv);
			String viewnameString = getString(R.string.install_app_name_view);
			
			tView.setText(String.format(viewnameString, appnameString));
		}
	}
	
	public void onSetTestPkgName(View v) {
		if (apkpath != null) {
			APKUtil.installAPK(apkpath);
		}
		Pingbackhandler.sendPB("安装APK","30");
		finish();
	}
	
	public void onCancel(View v) {
		finish();
	}
}
