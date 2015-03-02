package com.sogou.mobiletoolassist.ui;
import java.io.File;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ToolsTabFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v= inflater.inflate(R.layout.tools, container, false);
		
		return v;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		SharedPreferences appdata = getActivity().getSharedPreferences("AppData",
				getActivity().MODE_PRIVATE);
		if (!appdata.getBoolean("isFirstLaunch", true)) {// 不是首次登陆
			
			if (!appdata.getBoolean("isFloatWinOn", false)) {
				AssistActivity.isFloatwinon = false;
				MyImageView iv = ((MyImageView) getActivity().findViewById(R.id.floatwinsetview));
						iv.setImageResource(R.drawable.floatwinoff);
			}
		}else{
			appdata.edit().putBoolean("isFirstLaunch", false).commit();
		}
		
		String deafultpath = Environment.getExternalStorageDirectory().getPath();
		deafultpath += File.separator + "MobileTool/CrashReport";
		String observerpath = appdata.getString("obPath", deafultpath);	
		TextView v = (TextView) getActivity().findViewById(R.id.observerpath);
		v.setText(observerpath);
		
		int state = appdata.getInt("isWatching", AssistActivity.neverWatching);
		if(state == AssistActivity.isWatching){
			ImageView iv = (ImageView) getActivity().findViewById(R.id.observerview);
			iv.setImageResource(R.drawable.stop_observe);
			//((ImageView) getActivity().findViewById(R.id.scanfileview)).setEnabled(false);
			((ImageView) getActivity().findViewById(R.id.scanfileview)).setClickable(false);
		}
	}
	
	


	

}
