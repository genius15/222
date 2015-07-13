package com.sogou.mobiletoolassist.setting;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ConfirmTestAppActivity extends Activity {
	private String pkgnameString = null;
	private String appnameString = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_testapp_setted);
		Intent intent = getIntent();
		if (intent != null) {
			pkgnameString = intent.getStringExtra("pkgname");
			appnameString = intent.getStringExtra("appname");
			TextView tView = (TextView) findViewById(R.id.test_app_name);
			String viewnameString = getString(R.string.test_app_name_view);
			
			tView.setText(String.format(viewnameString, appnameString));
		}
	}
	
	public void onSetTestPkgName(View v) {
		if (pkgnameString != null) {
			AssistApplication.putString("pkgname", pkgnameString);
		}
		finish();
	}
	
	public void onCancel(View v) {
		finish();
	}
}
