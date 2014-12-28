package com.sogou.mobiletoolassist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.sogou.mobiletoolassist.service.ClearDataService;
import com.sogou.mobiletoolassist.service.FileObserverService;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class AssistActivity extends ActionBarActivity {
	public static String myTag = "Assist";
	public static String obPath = Environment.getExternalStorageDirectory().getPath()+File.separator+"MobileTool/CrashReport";
	public static int selectedidx = 0;
	private Stack<String> dirs = new Stack<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assist);
		
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		setPathView();//设置默认显示路径
		if(!UsefulClass.isServiceRunning(this,ClearDataService.class.getName())){
			Intent it = new Intent(this,ClearDataService.class);			
			this.startService(it);
			Log.d(myTag, "cleardataservice start");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_assist,
					container, false);
			return rootView;
		}
	}
	
	
	private void setPathView(){
		TextView v = (TextView)this.findViewById(R.id.textView1);
		v.setText(obPath);
	}
	
	private void ShowDialog(final String path){
		
		final File tmp = new File(path);
		File files[] = tmp.listFiles();
		selectedidx = 0;//每次进入都重置
		ArrayList<String> paths = new ArrayList<String>(); 
		if(files != null){
			for(File apath :files){
				if(apath.isDirectory()){
					paths.add(apath.toString());
				}
			}
		}
		final String spaths[] = (String[])paths.toArray(new String[paths.size()]);
		AlertDialog ad = new AlertDialog.Builder(this)
		.setTitle("选择一个要监控的文件夹")
		.setSingleChoiceItems(spaths, 0,new DialogInterface.OnClickListener(){
			@Override
		     public void onClick(DialogInterface dialog, int which) {
				selectedidx = which;//不知道为什么用单选列表，点击进入时which是无效值
		     }
		})
		.setNegativeButton("上一层", new DialogInterface.OnClickListener(){
			@Override
		     public void onClick(DialogInterface dialog, int which) {
				if(dirs.isEmpty()){
					Toast.makeText(getApplicationContext(), "已经到达根目录了", Toast.LENGTH_LONG).show();
					ShowDialog(tmp.getPath());
					return;
				}
				ShowDialog(dirs.pop());
		     }
		})
		.setNeutralButton("进入", new DialogInterface.OnClickListener(){
			@Override
		     public void onClick(DialogInterface dialog, int which) {
				dirs.add(path);
				ShowDialog(spaths[selectedidx]);
		     }
		})
		.setPositiveButton("选择", new DialogInterface.OnClickListener(){
			@Override
		     public void onClick(DialogInterface dialog, int which) {
				if(spaths != null && spaths.length > selectedidx){
					obPath = spaths[selectedidx];
					setPathView();
				}
		     }
		})
		.show();
		
	}

	public void onSelectClick(View arg0) {		
		File file = Environment.getExternalStorageDirectory();
		ShowDialog(file.getPath());
		
	}
	public void onStartObserve(View v){
		if(!new File(obPath).exists()){
			Log.e(myTag, obPath + " does not exist");
			File p = new File(obPath);
			p.mkdirs();
		}
		Intent intent = new Intent(this, FileObserverService.class);
		intent.putExtra("observerpath", obPath);
		this.startService(intent);
		
		//v.setClickable(false);不可点击是不能触发点击事件，但是按钮看起来还是能点的
		v.setEnabled(false);
		this.findViewById(R.id.SelectPath).setEnabled(false);

	}
	
	
}
