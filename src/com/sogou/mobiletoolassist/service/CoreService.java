package com.sogou.mobiletoolassist.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.fileobserver.FileObserverThread;
import com.sogou.mobiletoolassist.util.MailSender;
import com.sogou.mobiletoolassist.util.ScreenshotforGINGERBREAD_MR1;
import com.sogou.mobiletoolassist.util.ScreenshotforJELLY_BEAN;
import com.sogou.mobiletoolassist.util.StateValue;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("HandlerLeak") 
public class CoreService extends Service {
	private static WindowManager wm = null;
	private static WindowManager.LayoutParams params = null;
	private View btn_floatView = null;
	private ImageButton clearBtn = null;
	private ImageButton screenshotBtn = null;
	private ImageButton nexthour = null;
	private ImageButton nextday = null;
	private ImageButton smallview = null;
	private ImageButton wifisetview = null;
	private ImageButton appmagBtn = null;
	private final IBinder binder = new MyBinder();
	private final static int hide = 0x0000001;
	public final static int screenshot = 0x0000002;
	private final static int visiable = 0x0000003;
	private static boolean isUninstalling = false;
	
	private FileObserverThread listener = null;
	private String observerpath = null;
	private String emailReceiver = null;
	
	
	public Handler fltwinhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CoreService.hide:
				wm.removeView(btn_floatView);
				break;
			case CoreService.screenshot:
				Uri uri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);// 系统自带提示音
				Ringtone rt = RingtoneManager.getRingtone(
						getApplicationContext(), uri);
				if (rt != null)
					rt.play();
				CoreService.ScreenShot();
				Message message = new Message();
				message.what = CoreService.visiable;
				fltwinhandler.sendMessage(message);
				break;
			case CoreService.visiable:
				wm.addView(btn_floatView, params);
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {

		return binder;
	}

	public class MyBinder extends Binder {
		public CoreService getService() {
			return CoreService.this;
		}
	}

	public native String memcreate(int mem);

	public native String memfree();

	static {
		System.loadLibrary("memCtrl");
	}

	@Override
	public void onCreate() {
		// createFloatView();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("study", "core service onstart");
	}
	private void init(String path){
		SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);  
		appdata.edit().putString("obPath", path).commit();  //防止被重启，把path保存到本地
		
	}
	
	public void startWatching(){			
		SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);
		String deafultpath = Environment.getExternalStorageDirectory().getPath();
		deafultpath += File.separator + "MobileTool/CrashReport";
		observerpath = appdata.getString("obPath", deafultpath);		
		emailReceiver = appdata.getString("mailReceiver", "pdatest@sogou-inc.com");
		if(listener!=null){
			listener.stopWatching();
			listener = null;
		}
		listener = new FileObserverThread(observerpath,emailReceiver);		
		listener.startWatching();
	}
	
	public void stopWatching(){
		listener.stopWatching();
		listener = null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("study", "core service onStartCommand");
		createFloatView();
		SharedPreferences appdata = this.getSharedPreferences("AppData", MODE_PRIVATE);
		
		int state = appdata.getInt("isWatching", AssistActivity.neverWatching);
		if(state == AssistActivity.isWatching){
			startWatching();
		}
		return Service.START_STICKY;// 表示被系统杀掉后需要重启
	}

	@Override
	public void onDestroy() {
		wm.removeView(btn_floatView);
		super.onDestroy();
	}
	/**
	 * 创建悬浮窗
	 */
	@SuppressLint("InflateParams")
	private void createFloatView() {
		SharedPreferences appdata = getSharedPreferences("AppData",
				MODE_PRIVATE);		
		if (!appdata.getBoolean("isFloatWinOn", true)){
			return;
		}
		if (btn_floatView != null) {
			wm.addView(btn_floatView, params);
			return;
		}
		btn_floatView = LayoutInflater.from(this).inflate(R.layout.floatwin,
				null);
		smallview = (ImageButton) btn_floatView.findViewById(R.id.fwinsmallview);
		smallview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!clearBtn.isShown() || !screenshotBtn.isShown()) {
					clearBtn.setVisibility(Button.VISIBLE);					
					screenshotBtn.setVisibility(Button.VISIBLE);
					nexthour.setVisibility(Button.VISIBLE);
					nextday.setVisibility(Button.VISIBLE);
					wifisetview.setVisibility(Button.VISIBLE);
					appmagBtn.setVisibility(Button.VISIBLE);
					smallview.setImageResource(R.drawable.floatwin);
				} else {
					wifisetview.setVisibility(Button.GONE);
					nextday.setVisibility(Button.GONE);
					nexthour.setVisibility(Button.GONE);
					clearBtn.setVisibility(Button.GONE);
					screenshotBtn.setVisibility(Button.GONE);
					appmagBtn.setVisibility(Button.GONE);
					smallview.setImageResource(R.drawable.floatwin_collapsed);
				}
			}

		});
		appmagBtn = (ImageButton) btn_floatView.findViewById(R.id.appmag);
		appmagBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(Settings.ACTION_APPLICATION_SETTINGS);				
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(it);
				
			}
			
		});
		nexthour = (ImageButton) btn_floatView.findViewById(R.id.nexthour);
		nexthour.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd",Locale.CHINA);				
				Calendar ca = Calendar.getInstance();				
				ca.add(Calendar.HOUR_OF_DAY, 1);
				String nowDate=format.format(ca.getTime());
				int hour = ca.get(Calendar.HOUR_OF_DAY);
				String nextTime = String.valueOf(hour)+"5500";
				String cmd = "date -s  "+nowDate+"."+nextTime;
				UsefulClass.processCmd(cmd);
			}
			
		});
		nextday = (ImageButton) btn_floatView.findViewById(R.id.nextday);
		nextday.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Calendar ca = Calendar.getInstance();
				ca.add(Calendar.DAY_OF_MONTH, 1);
				SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd",Locale.CHINA);
				String nowTime=format.format(ca.getTime());
				UsefulClass.processCmd("date -s "+nowTime+".000500");
			}});
		wifisetview = (ImageButton)btn_floatView.findViewById(R.id.wifiset);
		wifisetview.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

				Intent it = new Intent(Settings.ACTION_DATE_SETTINGS);
				
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(it);
			}
			
		});
		clearBtn = (ImageButton) btn_floatView.findViewById(R.id.cleardatabtn);
		clearBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CoreService.onClearBtn();
			}

		});
		screenshotBtn = (ImageButton) btn_floatView
				.findViewById(R.id.screenshotbtn);
		screenshotBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Message message = new Message();
						message.what = CoreService.hide;
						Message message1 = new Message();
						message1.what = CoreService.screenshot;
						fltwinhandler.sendMessage(message);
						fltwinhandler.sendMessageDelayed(message1, 500);
					}

				}).start();
			}

		});
		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);

		params = new WindowManager.LayoutParams();

		// 设置window type
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		/*
		 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */

		params.format = PixelFormat.RGBA_8888;

		// 设置Window flag
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */

		// 设置悬浮窗的长得宽

		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.LEFT; // 调整悬浮窗口至左上角
		params.x = 0;
		params.y = 0;
		// 需要增加增加system.alert_window权限
		wm.addView(btn_floatView, params);
		appdata.edit().putBoolean("isFloatWinOn", true).commit();
		smallview.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;
			int paramX, paramY;

			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					paramX = params.x;
					paramY = params.y;

					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
						// 不这么做太灵敏了，明明我是在点击你移动个毛线啊

						break;
					}
					params.x = paramX + dx;
					params.y = paramY + dy;

					// 更新悬浮窗位置
					wm.updateViewLayout(btn_floatView, params);

					break;
				case MotionEvent.ACTION_UP:
					int dx1 = (int) event.getRawX() - lastX;
					int dy1 = (int) event.getRawY() - lastY;
					if (Math.abs(dx1) < 5 && Math.abs(dy1) < 5) {
						smallview.performClick();
					}

				}
				return true;
			}
		});
		
	}

	public static void onClearBtn() {
		Context ctx = AssistApplication.getContext();
		if (ctx == null) {
			Log.e(AssistActivity.myTag, "ctx 是空的");
			return;
		}
		if (!UsefulClass.hasappnamedxxx(ctx, "com.sogou.androidtool")) {
			Toast.makeText(ctx, "没有安装助手", Toast.LENGTH_SHORT).show();
			return;
		}
		String cmd = "pm clear com.sogou.androidtool";
		Toast.makeText(ctx, "准备清理数据~", Toast.LENGTH_SHORT).show();
		if (UsefulClass.processCmd(cmd) == StateValue.success) {
			Toast.makeText(ctx, "清理数据完毕~", Toast.LENGTH_SHORT).show();
		}
	}

	public static boolean ScreenShot() {
		
		String path = "";
		if (Build.VERSION.SDK_INT > 13) {
			path = ScreenshotforJELLY_BEAN.shoot();
		} else {
			if (ScreenshotforGINGERBREAD_MR1.isInitialized())
				path = ScreenshotforGINGERBREAD_MR1.shoot();
		}

		File testpath = new File(path);
		if (!testpath.exists()) {
			Toast.makeText(AssistApplication.getContext(), "截图文件不在，请检查sd卡是否正常",
					Toast.LENGTH_LONG).show();
			
			return false;
		}
		String info = UsefulClass.getDeviceInfo();
		String title = info + "【截图】";
		info += "</br>";
		info += UsefulClass.getZSPkgInfo();
		SharedPreferences appdata = AssistApplication.getContext()
				.getSharedPreferences("AppData", MODE_PRIVATE);
		String emailReceiver = appdata.getString("mailReceiver",
				"pdatest@sogou-inc.com");
		if (AssistApplication.getContext() != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) AssistApplication
					.getContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo == null || !mNetworkInfo.isConnected()) {
				Toast.makeText(AssistApplication.getContext(),
						"网络貌似有问题哦，邮件发不出去", Toast.LENGTH_LONG).show();
				
				return false;
			}
		}
		if (MailSender.sendTextMail(title, info, path,
				new String[] { emailReceiver })) {
			String emailReceivername = appdata.getString("name", "pdatest");
			Toast.makeText(AssistApplication.getContext(),
					"截图完毕，" + emailReceivername + "同学请静候邮件~", Toast.LENGTH_LONG)
					.show();
			File tmp = new File(path);
			if (tmp.exists())
				tmp.delete();
		} else {
			Toast.makeText(AssistApplication.getContext(), "发送邮件异常，可能是读截图失败了",
					Toast.LENGTH_LONG).show();
		}
		
		return true;
	}

	private Runnable uinstallrun = new Runnable() {

		@Override
		public void run() {
			CoreService.isUninstalling = true;
			Context ctx = AssistApplication.getContext();
			PackageManager pkgmgr = (PackageManager) ctx.getPackageManager();
			List<PackageInfo> allapps = pkgmgr.getInstalledPackages(0);
			List<PackageInfo> alluserapps = new ArrayList<PackageInfo>();
			for (PackageInfo app : allapps) {
				if ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					alluserapps.add(app);// 过滤所有非系统应用
				}
			}
			if (alluserapps.size() > 30) {
				Toast.makeText(ctx, "app太多，清理较慢，先去喝杯水吧~", Toast.LENGTH_SHORT)
						.show();
			}
			allapps = null;
			if (alluserapps == null || alluserapps.isEmpty()) {
				Toast.makeText(ctx, "你的手机没有已安装应用~", Toast.LENGTH_SHORT).show();
				CoreService.isUninstalling = false;
				return;
			}
			Toast.makeText(ctx, "开始清理app", Toast.LENGTH_SHORT).show();

			String cmd = "pm uninstall ";
			for (PackageInfo app : alluserapps) {
				if (app.packageName != null
						&& app.packageName.length() != 0
						&& !app.packageName.equals("com.sogou.androidtool")
						&& !app.packageName
								.equals("com.sogou.mobiletoolassist")
						&& !app.packageName
								.equals("com.sohu.inputmethod.sogou")
						&& !app.packageName
								.equals("com.speedsoftware.rootexplorer")) {

					if (StateValue.unroot == UsefulClass.processCmd(cmd
							+ app.packageName)) {
						Toast.makeText(ctx, "获取root权限失败！！！", Toast.LENGTH_SHORT)
								.show();
						break;
					}
				}
			}
			Toast.makeText(ctx, "已卸载所有app", Toast.LENGTH_LONG).show();
			CoreService.isUninstalling = false;
		}
	};

	public void uninstallAPPs() {
		if (isUninstalling)
			return;
		fltwinhandler.post(uinstallrun);
	}

	public void floatwinswitch(boolean set) {
		if (wm == null || btn_floatView == null || params == null) {
			createFloatView();
			return;
		}
		if (set) {			
			wm.addView(btn_floatView, params);
		} else {
			wm.removeView(btn_floatView);
		}
	}
}
