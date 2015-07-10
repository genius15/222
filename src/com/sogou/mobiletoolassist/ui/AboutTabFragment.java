package com.sogou.mobiletoolassist.ui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.adapter.ActionsAdapter;
import com.sogou.mobiletoolassit.infostatic.Pingbackhandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AboutTabFragment extends Fragment {
	private View view = null;
	private ToggleButton ftpsetButton = null;
	static final String ACTION_START_FTPSERVER = "be.ppareit.swiftp.ACTION_START_FTPSERVER";
	static final String ACTION_STOP_FTPSERVER = "be.ppareit.swiftp.ACTION_STOP_FTPSERVER";
	static final String FTPSERVER_STARTED = "be.ppareit.swiftp.FTPSERVER_STARTED";
	static final String FTPSERVER_STOPPED = "be.ppareit.swiftp.FTPSERVER_STOPPED";
	private ListView actionsLView = null;
	private Activity context = null;
	private ActionsAdapter actionsAdapter = null;
	
	public ArrayList<String> getActions() {
		if (actionsAdapter != null) {
			return actionsAdapter.getData();
		}
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.broadcastsetting, container, false);
		context = getActivity();
		return view;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		ftpsetButton = (ToggleButton) context.findViewById(
				R.id.scheduleToggle);

		IntentFilter intents = new IntentFilter();
		intents.addAction(FTPSERVER_STARTED);
		intents.addAction(FTPSERVER_STOPPED);
		context.registerReceiver(mStartStopReceiver, intents);
		ftpsetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onSetFtp();
			}

		});
		boolean State = context.getSharedPreferences(
				getString(R.string.cfg_ftp_state), Context.MODE_PRIVATE)
				.getBoolean("isRunning", false);
		if (State) {
			ftpsetButton.setChecked(true);
			ftpsetButton.setText(getString(R.string.ftpup));
		} else {
			ftpsetButton.setChecked(false);
			ftpsetButton.setText(getString(R.string.ftpdown));
		}

		actionsLView = (ListView) context.findViewById(R.id.actionlv);
		actionsAdapter = new ActionsAdapter();
		actionsLView.setAdapter(actionsAdapter);
		
		
		boolean issending = context.getSharedPreferences(
				getString(R.string.cfg_appdata), Context.MODE_PRIVATE)
				.getBoolean(getString(R.string.issending), false);
		Button button = (Button) context.findViewById(
				R.id.scheduleTaskBtn);
		EditText eText = (EditText) context.findViewById(
				R.id.actionFreqET);
		eText.setEnabled(!issending);
		
		if (issending) {
			button.setText(R.string.actionswitchoff);

		} else {
			button.setText(R.string.actionswitchon);
		}
		button.setEnabled(false);
		TextView tView = (TextView) context.findViewById(R.id.last_sended_time);
		String lasttime = context.getSharedPreferences(
				getString(R.string.cfg_appdata), Context.MODE_PRIVATE)
				.getString(context.getString(R.string.last_sended_time),
						"never");
		tView.setText(String.format(
				context.getString(R.string.last_sended_time), lasttime));
		
		new ReadActionsFromFile(button,actionsAdapter).execute();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		context.unregisterReceiver(mStartStopReceiver);
	}

	public void onSetFtp() {
		if (Build.VERSION.SDK_INT < 15) {
			Toast.makeText(context,
					getString(R.string.ttFtpVersionLimit), Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (!ftpsetButton.isChecked()) {// 点击后状态会立即改变，所以这里是改变后的状态
			Intent startIntent = new Intent(ACTION_STOP_FTPSERVER);
			
			context.sendBroadcast(startIntent);
			ftpsetButton.setChecked(true);
			ftpsetButton.setText(getString(R.string.ftpdowning));
		} else {
			Intent startIntent = new Intent(ACTION_START_FTPSERVER);
			context.sendBroadcast(startIntent);
			ftpsetButton.setChecked(false);
			ftpsetButton.setText(getString(R.string.ftpuping));
		}

	}

	BroadcastReceiver mStartStopReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Swiftp: notify user if ftp server is running or not
			if (intent.getAction().equals(FTPSERVER_STARTED)) {
				ftpsetButton.setChecked(true);
				ftpsetButton.setText(getString(R.string.ftpup));
				Pingbackhandler.sendPB("FTP文件互传","60");
			} else if (intent.getAction().equals(FTPSERVER_STOPPED)) {
				ftpsetButton.setChecked(false);
				ftpsetButton.setText(getString(R.string.ftpdown));
			}
		}
	};

	
	private class ReadActionsFromFile extends AsyncTask<Void, Void, ArrayList<String>>{
		private Button sendBtn = null;
		private ActionsAdapter actionsAdapter = null;
		public ReadActionsFromFile(Button btn, ActionsAdapter adp) {
			super();
			sendBtn = btn;
			actionsAdapter = adp;
		}
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ArrayList<String> actions = new ArrayList<>();
			String actionsfilepath = Environment.getExternalStorageDirectory()
					.getPath()
					+ File.separator
					+ context.getString(R.string.actionsfilename);
			File actionfile = new File(actionsfilepath);
			InputStream inputStream = null;
			try {
				if (actionfile.exists()) {
					inputStream = new FileInputStream(actionfile);
				} else {
					inputStream = context.getAssets()
							.open("build_in_actions");
				}
				BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;
				while ((line = bReader.readLine())!=null) {
					actions.add(line);
				}
				bReader.close();
				inputStream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return actions;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> actions){
			//该方法在主线程中执行，如果在doinbackground中更新UI会报异常，UI只能在主线程中更新
			if (actionsAdapter != null) {
				actionsAdapter.updateData(actions);
			}
			if (sendBtn != null) {
				sendBtn.setEnabled(true);
			}
		}
	}
}

