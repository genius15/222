package com.sogou.mobiletoolassist.ui;

import java.io.File;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.appmanager.APKUtil;
import com.sogou.mobiletoolassist.appmanager.SelfUpdate;
import com.sogou.mobiletoolassist.service.CoreService;
import com.sogou.mobiletoolassist.setting.GlobalSetting;
import com.sogou.mobiletoolassist.setting.TestedAppSetting;
import com.sogou.mobiletoolassist.util.JsonTestResultHandle;
import com.sogou.mobiletoolassist.util.NetworkUtil;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ToolsTabFragment extends Fragment implements Response.Listener<SelfUpdate>, Response.ErrorListener{

	private CheckBox jsonCheckBox = null;
	private String update = "http://10.129.157.174/update/update.php";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tools, container, false);
		setHasOptionsMenu(true);
		NetworkUtil.get(update, SelfUpdate.class, this, this);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences appdata = getActivity().getSharedPreferences(
				getString(R.string.cfg_appdata), getActivity().MODE_PRIVATE);
		if (!appdata.getBoolean("isFirstLaunch", true)) {// �����״ε�½

			if (!appdata.getBoolean("isFloatWinOn", false)) {
				AssistActivity.isFloatwinon = false;
				MyImageView iv = ((MyImageView) getActivity().findViewById(
						R.id.floatwinsetview));
				iv.setImageResource(R.drawable.floatwinoff);
			}
		} else {
			appdata.edit().putBoolean("isFirstLaunch", false).commit();
		}

		String deafultpath = Environment.getExternalStorageDirectory()
				.getPath();
		deafultpath += File.separator + "MobileTool/CrashReport";
		String observerpath = appdata.getString("obPath", deafultpath);
		TextView v = (TextView) getActivity().findViewById(R.id.observerpath);
		v.setText(observerpath);

		int state = appdata.getInt("isWatching", AssistActivity.neverWatching);
		if (state == AssistActivity.isWatching) {
			ImageView iv = (ImageView) getActivity().findViewById(
					R.id.observerview);
			iv.setImageResource(R.drawable.stop_observe);
			// ((ImageView)
			// getActivity().findViewById(R.id.scanfileview)).setEnabled(false);
			((ImageView) getActivity().findViewById(R.id.scanfileview))
					.setClickable(false);
		}

		jsonCheckBox = (CheckBox) getActivity().findViewById(R.id.cb_sendjson);

		SharedPreferences spPreferences = getActivity().getSharedPreferences(
				"AppData", Context.MODE_PRIVATE);
		boolean needSend = spPreferences.getBoolean("needSend", false);
		jsonCheckBox.setChecked(needSend);
		if (needSend) {
			String rec = AssistApplication.getEmailAddr();
			if (rec == null) {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.NoContactSetted),
						Toast.LENGTH_SHORT).show();
				return;
			}
			JsonTestResultHandle.sendJsonTestResult(rec);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		inflater.inflate(R.menu.tool_set_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.setting:
			Intent intent = new Intent(getActivity(),TestedAppSetting.class);
			//Intent intent = new Intent(getActivity(),GlobalSetting.class);
			//startActivityForResult(intent, 999);
			startActivity(intent);
			break;

		default:
			break;
		}
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if (requestCode == 999) {
			if (resultCode == 0 && intent != null) {
				String pkgString = intent.getStringExtra("pkgname");
				AssistApplication.putString("pkgname", pkgString);
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResponse(SelfUpdate response) {
		if (response != null) {
			PackageManager pManager = getActivity().getPackageManager();
			PackageInfo info = null;
			try {
				info = pManager.getPackageInfo("com.sogou.mobiletoolassist", 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				return;
			}
			
			if (response.vc > info.versionCode) {
//				Intent intent = new Intent(getActivity(),CoreService.class);
//				intent.putExtra("dl", response.dl);
//				getActivity().startService(intent);
				AssistApplication.ShowToast("哆啦A梦有新版，正在下载,请不要退出哦");		
				final String dlString = response.dl;
				new Thread(new Runnable() {
					
					@Override
					public void run() {
									
						UsefulClass.Download(dlString, "/sdcard/update.apk");
						APKUtil.installAPKNotSlience("/sdcard/update.apk");	
						
					}
				}).start();
				
			}
			
		}
		
	}
}
