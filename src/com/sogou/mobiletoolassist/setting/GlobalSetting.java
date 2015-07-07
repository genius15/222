package com.sogou.mobiletoolassist.setting;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GlobalSetting extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		String pkgname = AssistApplication.getString("pkgname");
		TextView tView = (TextView) findViewById(R.id.app_pkg_name);
		if (tView != null && pkgname != null) {
			tView.setText(pkgname);
		}
		
	}
	
	
	public void onSetPkgName(View view){
		
		EditText eText = (EditText) findViewById(R.id.app_pkg_name_edit);
		String pkgString = eText.getText().toString();
		if (pkgString == null || pkgString.isEmpty()) {
			AssistApplication.ShowToast("空包名？");
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("pkgname", pkgString);
		setResult(0, intent);
		finish();
	}
	
	public void onCancel(View v) {
		
		setResult(-1, null);
		finish();
	}
}
