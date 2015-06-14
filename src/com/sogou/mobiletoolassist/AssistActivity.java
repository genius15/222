
package com.sogou.mobiletoolassist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import be.ppareit.swiftp.gui.FsPreferenceActivity;

import com.sogou.mobiletoolassist.service.CoreService;
import com.sogou.mobiletoolassist.ui.AboutTabFragment;
import com.sogou.mobiletoolassist.ui.ReceiversFragment;
import com.sogou.mobiletoolassist.ui.ToolsTabFragment;
import com.sogou.mobiletoolassist.util.ScreenshotforGINGERBREAD_MR1;
import com.sogou.mobiletoolassist.util.ShellCommand;
import com.sogou.mobiletoolassist.util.UsefulClass;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AssistActivity extends FragmentActivity {
	public static String myTag = "Assist";
	public static String obPath = Environment.getExternalStorageDirectory()
			.getPath() + File.separator + "MobileTool/CrashReport";

	public static int selectedidx = 0;
	public static String receiver = null;
	@SuppressWarnings("unused")
	private AssistApplication app = new AssistApplication();
	private Stack<String> dirs = new Stack<String>();
	private CoreService backservice;
	private String basedir = null;
	public static boolean isFloatwinon = true;
	private Fragment toolsFrag = null;
	private Fragment aboutFrag = null;
	private Fragment recFrag = null;
	private ImageView toolsTab = null;
	private ImageView contactTab = null;
	private ImageView aboutTab = null;
	private CheckBox jsonCheckBox = null;
	private ToggleButton ftpsetButton = null;
	public static String dataname = "AppData";
	public static int neverWatching = 0x00001000;
	public static int isWatching = neverWatching+1;
	public static int noWatching = neverWatching+2;
	public static String installedaction = "com.sogou.mobiletoolassist.action.installed";
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder localBinder) {
			backservice = ((CoreService.MyBinder) localBinder).getService();
		}

		public void onServiceDisconnected(ComponentName arg0) {
			backservice = null;
		}
	};
	private boolean isadded = false;
	private final static int uninstallapps = 900;
	private final static int installmt = uninstallapps + 1;
	private final static int generateFile = installmt+1;
	private final static int generateFolder = generateFile + 1;
	private final static int generateOver = generateFolder + 1;
	public Handler assistActhandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AssistActivity.uninstallapps:
				findViewById(R.id.uninstallview).setEnabled(false);
				if (backservice != null) {
					backservice.uninstallAPPs();
				}
				findViewById(R.id.uninstallview).setEnabled(true);
				break;
			case AssistActivity.generateFile:
				final int num = msg.getData().getInt("num");
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String path = Environment.getExternalStorageDirectory()+File.separator;
						for (int i = 0; i < num; i++) {
							File file = new File(path+i+".log");
							if (file.exists()) {
								file.delete();
							}
							try {
								file.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							file = null;
						}
						Message toa = new Message();
						toa.what = AssistActivity.generateOver;
						assistActhandler.sendMessage(toa);
					}
				}).start();
				break;
			case AssistActivity.generateFolder:
				final int num1 = msg.getData().getInt("num");
				new Thread(new Runnable() {
					public void run() {
						String path = Environment.getExternalStorageDirectory()+File.separator;
						for (int i = 0; i < num1; i++) {
							File file = new File(path+i);
							if (file.exists()) {
								file.delete();
							}
							file.mkdirs();
							file = null;
						}
						Message toa = new Message();
						toa.what = AssistActivity.generateOver;
						assistActhandler.sendMessage(toa);
					}
				}).start();
				break;
			case AssistActivity.generateOver:
				Toast.makeText(AssistApplication.getContext(),"生成完毕",Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("study", "assist act oncreate");
		setContentView(R.layout.activity_assist);

		Log.i("study", AssistActivity.class.getName());
		// if (!appdata.getBoolean("isscadded", false)) {
		// addShortcut();
		// appdata.edit().putBoolean("isscadded", true).commit();
		// }
		//UsefulClass.processCmd("/data/local/tcpdump -p -vv -s 0 -w /sdcard/zscapture.pcap");

		if (!UsefulClass
				.isServiceRunning(this, CoreService.class.getName())) {
			Intent it = new Intent(this, CoreService.class);
			startService(it);
		}
		if (!ScreenshotforGINGERBREAD_MR1.isInitialized()) {
			ScreenshotforGINGERBREAD_MR1.init(this);
		}

		Intent bindintent = new Intent(this, CoreService.class);
		bindService(bindintent, mConnection, Context.BIND_AUTO_CREATE);

		initEmailReceiver();

		// 设置策略，使其不会抛出networkonMainThreadException
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
		// detectLeakedClosableObjects()
		try {
			if (basedir == null)
				basedir = getBaseContext().getFilesDir().getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (toolsTab == null || contactTab == null || aboutTab == null) {
			DisplayMetrics dm = new DisplayMetrics();
			Display display = getWindowManager().getDefaultDisplay();
			display.getMetrics(dm);
			int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
			int viewWidth = screenWidth / 3;
			toolsTab = (ImageView) findViewById(R.id.toolsTab);
			contactTab = ((ImageView) findViewById(R.id.receiverListTab));
			aboutTab = ((ImageView) findViewById(R.id.aboutTab));
			LayoutParams para = toolsTab.getLayoutParams();
			para.width = viewWidth;
			toolsTab.setLayoutParams(para);
			para = contactTab.getLayoutParams();
			para.width = viewWidth;
			contactTab.setLayoutParams(para);
			para = aboutTab.getLayoutParams();
			para.width = screenWidth - 2 * viewWidth;
			aboutTab.setLayoutParams(para);
			onClickToolsTab(toolsTab);			
		}
//		ActivityManager aManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		List<RunningAppProcessInfo> processInfos=aManager.getRunningAppProcesses();
//		for (RunningAppProcessInfo runningAppProcessInfo : processInfos) {
//			Log.i("process", runningAppProcessInfo.processName);
//			Log.i("process", String.valueOf(runningAppProcessInfo.pid));
//			Log.i("process", String.valueOf(runningAppProcessInfo.uid));
//		}
		
//		IntentFilter intents = new IntentFilter();
//        intents.addAction(FTPSERVER_STARTED);
//        intents.addAction(FTPSERVER_STOPPED);
//        registerReceiver(mStartStopReceiver, intents);
	}
	public BroadcastReceiver broadreceiver = new BroadcastReceiver() {
		 
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(installedaction)) {
                ImageView view = (ImageView) findViewById(R.id.installmt);
                view.setImageResource(R.drawable.installmt);
            } 
        }
    };
	@Override
	public void onPause() {
		super.onPause();
		Log.i("study", "assist act onPause");
	}

	@Override
	public void onStart() {
		super.onStart();
		//RecoveryState();
		Log.i("study", "assist act onStart");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.i("study", "assist act onRestart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("study", "assist act onResume");
	}

	@Override
	public void onStop() {
		super.onStop();
		
		Log.i("study", "assist act onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
		//unregisterReceiver(mStartStopReceiver);
		Log.i("study", "assist act onDestroy");
	}

	private void deleteShortcut() {
		// Build the intent for the chosen application
		Intent appIntent = new Intent(Intent.ACTION_VIEW);
		appIntent.setComponent(new ComponentName("com.sogou.mobiletoolassist",
				".AssistActivity"));

		// Build the intent for deleting shortcut
		Intent shortcutIntent = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, appIntent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "zs测试助手");
		shortcutIntent.putExtra("duplicate", false);

		sendBroadcast(shortcutIntent);
	}

	private void addShortcut() {
		// deleteShortcut();
		Intent addit = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
				R.drawable.ic_launcher);
		addit.putExtra(Intent.EXTRA_SHORTCUT_NAME, "zs测试助手");
		addit.putExtra("duplicate", false);
		addit.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		Intent startit = new Intent(Intent.ACTION_MAIN);
		startit.addCategory(Intent.CATEGORY_LAUNCHER);
		startit.setClass(AssistApplication.getContext(), AssistActivity.class);
		startit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		addit.putExtra(Intent.EXTRA_SHORTCUT_INTENT, startit);
		this.sendBroadcast(addit);
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
		// if (keyCode == KeyEvent.KEYCODE_BACK) {
		// moveTaskToBack(false);
		// return true;
		// }
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
		SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);  
		appdata.edit().putString("obPath", obPath).commit();
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
	public void onInstallMt(View arg) {
		backservice.installmt();
//		ImageView view = (ImageView) findViewById(R.id.installmt);
//		view.setImageResource(R.drawable.installmting);
	}
	private void switchTab(Fragment tab) {
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.replace(R.id.toolsLinear, tab);
		transaction.commit();
	}

	public void onStartObserve(View v) {
		

		if (!new File(obPath).exists()) {
			Log.e(myTag, obPath + " does not exist");
			File p = new File(obPath);
			p.mkdirs();
		}
//		Intent intent = new Intent(this, FileObserverService.class);
//		intent.putExtra("observerpath", obPath);
//		this.startService(intent);
		SharedPreferences appdata = this.getSharedPreferences(dataname, MODE_PRIVATE);
		int state = appdata.getInt("isWatching", AssistActivity.neverWatching);
		if(state == AssistActivity.isWatching){
			ImageView iv = (ImageView) findViewById(R.id.observerview);
			iv.setImageResource(R.drawable.observer);
			((ImageView)findViewById(R.id.scanfileview)).setClickable(true);
			backservice.stopWatching();
			appdata.edit().putInt("isWatching", noWatching).commit();
			Toast.makeText(this, "已经停止监控", Toast.LENGTH_SHORT).show();
		}else{
			appdata.edit().putInt("isWatching", isWatching).commit();
			backservice.startWatching();
			ImageView iv = (ImageView) findViewById(R.id.observerview);
			iv.setImageResource(R.drawable.stop_observe);
			((ImageView)findViewById(R.id.scanfileview)).setClickable(false);
			Toast.makeText(this, "已经开始监控，选择文件夹功能将会被禁用", Toast.LENGTH_SHORT).show();
		}		
		
	}

	public void onUninstallAPPS(View v) {
		AlertDialog ad = new AlertDialog.Builder(this)
		.setTitle("确定要删除所有app？")
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						
					}
				})
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Message msg = new Message();
				msg.what = AssistActivity.uninstallapps;
				assistActhandler.sendMessage(msg);
			}
		}).show();

		
	}

	public static HashMap<String, String> nameEmailMap = new HashMap<String, String>();
	static {
		nameEmailMap.put("徐文静", "xuwenjing@sogou-inc.com");
		nameEmailMap.put("唐志刚", "tangzhigang@sogou-inc.com");
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
		

	}
	public static HashMap<String, String> nameipMap = new HashMap<String, String>();
	static {
		nameipMap.put("张帅", "10.129.157.174");
		nameipMap.put("徐文静", "10.129.156.128");
		nameipMap.put("田丹丹", "10.129.157.134");
		nameipMap.put("谷晓沙", "10.129.156.78");
		nameipMap.put("廖振华", "10.129.156.103");
		nameipMap.put("王灿", "10.129.156.42");
		nameipMap.put("王坤", "10.129.158.46");
		nameipMap.put("董宏博", "10.129.157.28");
		nameipMap.put("孙静", "10.129.156.69");
		nameipMap.put("赵喜宁", "10.129.157.249");
		nameipMap.put("商丽丽", "");
		nameipMap.put("唐志刚", "10.129.156.164");

	}
	public final static String names[] = { "徐文静", "唐志刚" ,"田丹丹", "张帅", "谷晓沙", "廖振华",
			"王灿", "王坤", "董宏博", "孙静", "赵喜宁", "商丽丽"};

	public void onSetMailReceiver(View v) {
		SharedPreferences data = AssistApplication.getContext()
				.getSharedPreferences("AppData", MODE_PRIVATE);
		String recname = data.getString("name", "");
		int idx = 0;
		for (int i = 0; i < names.length; ++i) {
			if (names[i].equals(recname)) {
				idx = i;
				break;
			}
		}
		selectedidx = idx;// 设置默认值，如果进去后没有切换，那么默认是这个
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
						SharedPreferences appdata = AssistApplication
								.getContext().getSharedPreferences("AppData",
										MODE_PRIVATE);

						appdata.edit()
								.putString(getString(R.string.cfgmailreceiver),
										getString(R.string.cfg_key_recevier))
								.commit();
						appdata.edit().putString("name", names[selectedidx])
								.commit();
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

	public void onMemCtrl(View v) {
		if (!isadded) {// 如果是还没填充则填充
			EditText ev = (EditText) this.findViewById(R.id.memEdit);
			String mem = ev.getText().toString();
			try {
				int memi = Integer.valueOf(mem);
				if (memi < 1000) {
					Toast.makeText(this, memfree(memi), Toast.LENGTH_SHORT)
							.show();
					isadded = true;
					findViewById(R.id.memEdit).setEnabled(false);
				} else
					Toast.makeText(this, "超过1000了，小点吧", Toast.LENGTH_SHORT)
							.show();
			} catch (NumberFormatException e) {
				e.printStackTrace();
				Toast.makeText(this, "你输入的不是数字吗", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, memfree(), Toast.LENGTH_SHORT).show();
			findViewById(R.id.memEdit).setEnabled(true);
			isadded = false;
		}

		if (!isadded) {
			((Button) v).setText(R.string.CreateMem);
		} else {
			((Button) v).setText(R.string.freemem);
		}
	}

	private String memfree(int size) {
		return backservice.memcreate(size);
	}

	private String memfree() {
		return backservice.memfree();
	}

	public void onFloatwinSet(View v) {
		SharedPreferences appdata = getSharedPreferences("AppData",
				MODE_PRIVATE);
		appdata.edit().putBoolean("isFloatWinOn", !isFloatwinon).commit();
		if (isFloatwinon) {
			backservice.floatwinswitch(false);
			((ImageView) v).setImageResource(R.drawable.floatwinoff);
			isFloatwinon = false;
		} else {
			backservice.floatwinswitch(true);
			((ImageView) v).setImageResource(R.drawable.floatwinon);
			isFloatwinon = true;
		}
		
	}

	public void onClickToolsTab(View v) {
		((ImageView) v).setImageResource(R.drawable.toolstabnewpressed);
		((ImageView) findViewById(R.id.receiverListTab))
				.setImageResource(R.drawable.contacts);
		((ImageView) findViewById(R.id.aboutTab))
				.setImageResource(R.drawable.abouttab);
		if (toolsFrag == null) {
			toolsFrag = new ToolsTabFragment();
		}
		switchTab(toolsFrag);
		
	}

	public void onClickRecTab(View v) {
		((ImageView) v).setImageResource(R.drawable.contactspressed);
		((ImageView) findViewById(R.id.aboutTab))
				.setImageResource(R.drawable.abouttab);
		((ImageView) findViewById(R.id.toolsTab))
				.setImageResource(R.drawable.toolstabnew);
		if (recFrag == null) {
			recFrag = new ReceiversFragment();
		}
		switchTab(recFrag);
	}

	public void onClickAbout(View v) {
		((ImageView) v).setImageResource(R.drawable.abouttabpressed);
		((ImageView) findViewById(R.id.receiverListTab))
				.setImageResource(R.drawable.contacts);
		((ImageView) findViewById(R.id.toolsTab))
				.setImageResource(R.drawable.toolstabnew);
		if (aboutFrag == null) {
			aboutFrag = new AboutTabFragment();
		}
//		if (ftpsetButton == null) {
//			ftpsetButton = (ToggleButton) aboutFrag.getView().findViewById(R.id.scheduleToggle);
//		}
		
		switchTab(aboutFrag);
	}
	public void onClickJsonTest(View v) {
		CheckBox cbBox = (CheckBox) v;
		boolean needSend = cbBox.isChecked();
		this.getSharedPreferences("AppData",Context.MODE_PRIVATE).edit().putBoolean("needSend", needSend).commit();
	}
	
	public void onGenerateFolder(View v){
		EditText ev = (EditText) this.findViewById(R.id.memEdit);
		String num = ev.getText().toString();
		int n = 500;
		try {
			n = Integer.parseInt(num);
		} catch (NumberFormatException e) {
			Toast.makeText(this, "输入的内容不是数字,将默认生成500个", Toast.LENGTH_LONG).show();
			return;
		}
		
		Message msg  = new Message();
		msg.what = AssistActivity.generateFolder;
		Bundle bundle = new Bundle();   
		bundle.putInt("num", n);
		msg.setData(bundle);
		assistActhandler.sendMessage(msg);
	}
	
	public void onGenerateEmptyFile(View v){
		EditText ev = (EditText) this.findViewById(R.id.memEdit);
		String num = ev.getText().toString();
		int n = 500;
		try {
			n = Integer.parseInt(num);
		} catch (NumberFormatException e) {
			Toast.makeText(this, "输入的内容不是数字,将默认生成500个", Toast.LENGTH_LONG).show();
			return;
		}
		Message msg  = new Message();
		Bundle bundle = new Bundle();   
		bundle.putInt("num", n);
		msg.setData(bundle);
		msg.what = AssistActivity.generateFile;
		assistActhandler.sendMessage(msg);
	}
	
	public void onSendbroadcast(View v) {
		Message msg = new Message();
		msg.what = CoreService.sendBroadcast;
		backservice.fltwinhandler.sendMessage(msg);
		
	}
	
	public void onClickMaxHeapShow(View b) {
		ActivityManager mManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int mTotalSize = mManager.getMemoryClass();
		Toast.makeText(this, String.valueOf(mTotalSize), Toast.LENGTH_LONG).show();
	}
	
	public void testwifiset(View v) {
		//ToggleButton tbButton = (ToggleButton) findViewById(R.id.timesToggle);
		WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wifiManager.setWifiEnabled(true);
		
	}
	static final String ACTION_START_FTPSERVER = "be.ppareit.swiftp.ACTION_START_FTPSERVER";
    static final String ACTION_STOP_FTPSERVER = "be.ppareit.swiftp.ACTION_STOP_FTPSERVER";
    static final String FTPSERVER_STARTED = "be.ppareit.swiftp.FTPSERVER_STARTED";
    static final String FTPSERVER_STOPPED = "be.ppareit.swiftp.FTPSERVER_STOPPED";
	public void onSetFtp(View view) {
		if (Build.VERSION.SDK_INT < 15) {
			Toast.makeText(this, getString(R.string.ttFtpVersionLimit), Toast.LENGTH_LONG).show();
			return;
		}
		if (ftpsetButton.isChecked()) {//如果已经是开启的，则去关闭
			Intent startIntent = new Intent(ACTION_STOP_FTPSERVER);
	        sendBroadcast(startIntent);
		}else {
			Intent startIntent = new Intent(ACTION_START_FTPSERVER);
	        sendBroadcast(startIntent);
		}
		
	}
	
	BroadcastReceiver mStartStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Swiftp: notify user if ftp server is running or not
            if (intent.getAction().equals(FTPSERVER_STARTED)) {
            	ftpsetButton.setChecked(true);
                //mStatusText.setText("FTP Server is running");
            } else if (intent.getAction().equals(FTPSERVER_STOPPED)) {
            	ftpsetButton.setChecked(false);
                //mStatusText.setText("FTP Server is down");
            }
        }
    };
}
