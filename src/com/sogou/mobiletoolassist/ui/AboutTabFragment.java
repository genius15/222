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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
	private ArrayList<String> actions = null;
	private Activity context = null;

	public ArrayList<String> getActions() {
		return actions;
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
		ActionsAdapter adapter = new ActionsAdapter(getActionsFromFile());
		actionsLView.setAdapter(adapter);
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
		TextView tView = (TextView) context.findViewById(R.id.last_sended_time);
		String lasttime = context.getSharedPreferences(
				getString(R.string.cfg_appdata), Context.MODE_PRIVATE)
				.getString(context.getString(R.string.last_sended_time),
						"never");
		tView.setText(String.format(
				context.getString(R.string.last_sended_time), lasttime));
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
				// mStatusText.setText("FTP Server is running");
			} else if (intent.getAction().equals(FTPSERVER_STOPPED)) {
				ftpsetButton.setChecked(false);
				ftpsetButton.setText(getString(R.string.ftpdown));
				// mStatusText.setText("FTP Server is down");
			}
		}
	};

	private ArrayList<String> getActionsFromFile() {
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
//			int cnt = inputStream.available();
//			byte buf[] = new byte[cnt];
//			
//			inputStream.read(buf);
//			String actionstr = new String(buf);
//			String actionsArrString[] = actionstr.split("\r\n");
//			actions.addAll(Arrays.asList(actionsArrString));
			bReader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.actions = actions;
		if (actions.isEmpty()) {
			actions.add("no actions");
		}
		return actions;
	}
}

