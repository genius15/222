package com.sogou.mobiletoolassist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.sogou.mobiletoolassist.service.ClearDataService;
import com.sogou.mobiletoolassist.service.FileObserverService;
import com.sogou.mobiletoolassist.service.floatwin;
import com.sogou.mobiletoolassist.ui.MyImageView;
import com.sogou.mobiletoolassist.util.ScreenshotforGINGERBREAD_MR1;
import com.sogou.mobiletoolassist.util.ShellCommand;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AssistActivity extends Activity {
	public static String myTag = "Assist";
	public static String obPath = Environment.getExternalStorageDirectory()
			.getPath() + File.separator + "MobileTool/CrashReport";
	public static int selectedidx = 0;
	public static String receiver = null;
	@SuppressWarnings("unused")
	private assistApplication app = new assistApplication();
	private Stack<String> dirs = new Stack<String>();
	private floatwin backservice;
	private String basedir = null;

	private MyImageView scanfileview = null;
	private MyImageView observerview = null;
	private MyImageView UninsatllView = null;
	private MyImageView contactView = null;
	private MyImageView proxysetView = null;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder localBinder) {
			backservice = ((floatwin.MyBinder) localBinder).getService();
		}

		public void onServiceDisconnected(ComponentName arg0) {
			backservice = null;
		}
	};
	private boolean isadded = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assist);
		// if (savedInstanceState == null) {
		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.container, new PlaceholderFragment()).commit();
		// }
		setPathView();// 设置默认显示路径
		if (Build.VERSION.SDK_INT > 13) {
			if (!UsefulClass.isServiceRunning(this,
					ClearDataService.class.getName())) {
				Intent it = new Intent(this, ClearDataService.class);
				this.startService(it);
				Log.d(myTag, "cleardataservice start");
			}
		}
		// else{2.3手机悬浮窗在通知栏上面所以不需要通过通知来截图
		// if(!UsefulClass.isServiceRunning(this,NotificationBelowIceCreamSandwich.class.getName())){
		// Intent it = new Intent(this,NotificationBelowIceCreamSandwich.class);
		// this.startService(it);
		// Log.d(myTag, "NotificationBelowIceCreamSandwich start");
		// }
		// }
		if (!UsefulClass.isServiceRunning(this, floatwin.class.getName())) {
			Intent it = new Intent(this, floatwin.class);
			startService(it);
		}
		ScreenshotforGINGERBREAD_MR1.init(this);
		Intent bindintent = new Intent(AssistActivity.this, floatwin.class);
		bindService(bindintent, mConnection, Context.BIND_AUTO_CREATE);

		initEmailReceiver();

		//设置策略，使其不会抛出networkonMainThreadException
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork() 
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		
		try {
			basedir = getBaseContext().getFilesDir().getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scanfileview = (MyImageView) findViewById(R.id.scanfileview);
		scanfileview.setOnClickIntent(new MyImageView.OnViewClick(){

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				File file = Environment.getExternalStorageDirectory();
				ShowDialog(file.getPath());
			}
			
		});
		
		observerview = (MyImageView) findViewById(R.id.observerview);
//		observerview.setOnClickIntent(new MyImageView.OnViewClick(){
//
//			@Override
//			public void onClick() {
//				// TODO Auto-generated method stub
//				File file = Environment.getExternalStorageDirectory();
//				ShowDialog(file.getPath());
//			}
//			
//		});
	}

	private void initEmailReceiver() {
		SharedPreferences appdata = this.getSharedPreferences("AppData",
				MODE_PRIVATE);
		receiver = appdata.getString("mailReceiver", "");
		if (receiver.length() == 0) {
			appdata.edit().putString("mailReceiver", "pdatest@sogou-inc.com")
					.commit();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assist, menu);
		return true;
	}

	private void setPathView() {
		TextView v = (TextView) this.findViewById(R.id.observerpath);
		v.setText(obPath);
	}

	private void ShowDialog(final String path) {

		final File tmp = new File(path);
		File files[] = tmp.listFiles();
		selectedidx = 0;// 每次进入都重置
		ArrayList<String> paths = new ArrayList<String>();
		if (files != null) {
			for (File apath : files) {
				if (apath.isDirectory()) {
					paths.add(apath.toString());
				}
			}
		}
		final String spaths[] = (String[]) paths.toArray(new String[paths
				.size()]);
		@SuppressWarnings("unused")
		AlertDialog ad = new AlertDialog.Builder(this)
				.setTitle("选择一个要监控的文件夹")
				.setSingleChoiceItems(spaths, 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								selectedidx = which;// 不知道为什么用单选列表，点击进入时which是无效值
							}
						})
				.setNegativeButton("上一层",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (dirs.isEmpty()) {
									Toast.makeText(getApplicationContext(),
											"已经到达根目录了", Toast.LENGTH_LONG)
											.show();
									ShowDialog(tmp.getPath());
									return;
								}
								ShowDialog(dirs.pop());
							}
						})
				.setNeutralButton("进入", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dirs.add(path);
						ShowDialog(spaths[selectedidx]);
					}
				})
				.setPositiveButton("选择", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (spaths != null && spaths.length > selectedidx) {
							obPath = spaths[selectedidx];
							selectedidx = 0;
							setPathView();
						}
					}
				}).show();

	}

	public void onSelectClick(View arg0) {
		File file = Environment.getExternalStorageDirectory();
		ShowDialog(file.getPath());

	}

	public void onStartObserve(View v) {
		if (!new File(obPath).exists()) {
			Log.e(myTag, obPath + " does not exist");
			File p = new File(obPath);
			p.mkdirs();
		}
		Intent intent = new Intent(this, FileObserverService.class);
		intent.putExtra("observerpath", obPath);
		this.startService(intent);
		v.setEnabled(false);
		this.findViewById(R.id.SelectPath).setEnabled(false);
	}

	public void onUninstallAPPS(View v) {
		v.setEnabled(false);
		if (backservice != null) {
			backservice.uninstallAPPs();
		}
		v.setEnabled(true);
	}

	public static HashMap<String, String> nameEmailMap = new HashMap<String, String>();
	static {
		nameEmailMap.put("徐文静", "xuwenjing@sogou-inc.com");
		nameEmailMap.put("田丹丹", "tindandan@sogou-inc.com");
		nameEmailMap.put("张帅", "zhangshuai203407@sogou-inc.com");
		nameEmailMap.put("谷晓沙", "guxiaosha203822@sogou-inc.com");
		nameEmailMap.put("廖振华", "liaozhenhua@sogou-inc.com");
		nameEmailMap.put("王灿", "canwang@sogou-inc.com");
		nameEmailMap.put("王坤", "wangkun@sogou-inc.com");
		nameEmailMap.put("董宏博", "donghongbo@sogou-inc.com");
		nameEmailMap.put("孙静", "sunjing@sogou-inc.com");
		nameEmailMap.put("赵喜宁", "zhaoxining@sogou-inc.com");
		nameEmailMap.put("商丽丽", "shanglili@sogou-inc.com");
		nameEmailMap.put("唐志刚", "tangzhigang@sogou-inc.com");

	}
	public static HashMap<String, String> nameipMap = new HashMap<String, String>();
	static {
		nameipMap.put("张帅", "10.129.156.232");
	}
	public final static String names[] = { "徐文静", "田丹丹", "张帅", "谷晓沙", "廖振华",
			"王灿", "王坤", "董宏博", "孙静", "赵喜宁", "商丽丽", "唐志刚" };

	public void onSetMailReceiver(View v) {
		SharedPreferences data = assistApplication
				.getContext().getSharedPreferences("AppData",
						MODE_PRIVATE);
		String recname = data.getString("name", "");
		int idx = 0;
		for(int i = 0;i<names.length;++i){
			if(names[i].equals(recname)){
				idx = i;
				break;
			}
		}
		@SuppressWarnings("unused")
		AlertDialog ad = new AlertDialog.Builder(this)
				.setTitle("选择邮件接收者")
				.setSingleChoiceItems(names, idx,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								selectedidx = which;
							}
						})
				.setPositiveButton("选择", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						receiver = nameEmailMap.get(names[selectedidx]);
						SharedPreferences appdata = assistApplication
								.getContext().getSharedPreferences("AppData",
										MODE_PRIVATE);
						
						appdata.edit().putString("mailReceiver", receiver)
								.commit();
						appdata.edit().putString("name", names[selectedidx]).commit();
						SharedPreferences settings = PreferenceManager
								.getDefaultSharedPreferences(getBaseContext());
						settings.edit()
								.putString("proxyHost",
										nameipMap.get(names[selectedidx]))
								.commit();
						settings.edit().putString("proxyPort", "8888").commit();
						ShellCommand cmd = new ShellCommand();
						cmd.sh.runWaitFor(basedir + "/proxy.sh stop " + basedir);
						cmd.su.runWaitFor(basedir + "/redirect.sh stop");
						settings.edit().putBoolean("isEnabled", false).commit();
						selectedidx = 0;
					}
				}).show();

	}

	public void onSetProxyBtn(View v) {
		Intent intent = new Intent(this, ProxyActivity.class);
		this.startActivity(intent);
	}

	public void onMemCtrl(View v){
		if(!isadded){//如果是还没填充则填充
			EditText ev = (EditText) findViewById(R.id.memEdit);
	    	String mem = ev.getText().toString();
	    	try{
	    		int memi = Integer.valueOf(mem);
	    		if(memi < 1000){
	    			Toast.makeText(this,memfree(memi),Toast.LENGTH_SHORT).show();
	    			isadded = true;
	    			findViewById(R.id.memEdit).setEnabled(false);
	    		}
	    		else
	    			Toast.makeText(this, "超过1000了，小点吧", Toast.LENGTH_SHORT).show();
	    	}catch(NumberFormatException e){
	    		e.printStackTrace();
	    		Toast.makeText(this, "你输入的不是数字吗", Toast.LENGTH_LONG).show();
	    	}
		}else{
			Toast.makeText(this,memfree(),Toast.LENGTH_SHORT).show();
    		findViewById(R.id.memEdit).setEnabled(true);
    		isadded = false;
		}
		
    	
    	if(!isadded){
			((Button)v).setText(R.string.CreateMem);
		}
		else{
			((Button)v).setText(R.string.freemem);
		}
	}
	
	private String memfree(int size){
		return backservice.memcreate(size);
	}
	
	private String memfree(){
		return backservice.memfree();
	}
}
