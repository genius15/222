package com.sogou.mobiletoolassist.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.fileobserver.FileObserverThread;
import com.sogou.mobiletoolassist.receiver.SimuBroadcastReceiver;
import com.sogou.mobiletoolassist.util.FetchNewestMTApk;
import com.sogou.mobiletoolassist.util.MailSender;
import com.sogou.mobiletoolassist.util.ScreenshotforGINGERBREAD_MR1;
import com.sogou.mobiletoolassist.util.ScreenshotforJELLY_BEAN;
import com.sogou.mobiletoolassist.util.StateValue;
import com.sogou.mobiletoolassist.util.UsefulClass;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.format.DateFormat;
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
import android.widget.RemoteViews;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class CoreService extends Service implements OnClickListener {
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
	private final static int installmt = 0x0000004;
	private final static int downloadfailed = 0x0000005;
	public final static int sendBroadcast = 0x0000006;
	public final static int stopsendBroadcast = sendBroadcast + 1;
	private static boolean isUninstalling = false;
	public static String mtpathString = Environment
			.getExternalStorageDirectory().getPath() + File.separator;
	private FileObserverThread listener = null;
	private String observerpath = null;
	private String emailReceiver = null;
	public boolean isInstalling = false;
	private Thread sendBroadcastThd = null;
	public int freq = 0;
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
			case CoreService.installmt:
				// Intent installIntent = new Intent(Intent.ACTION_MAIN);
				// installIntent.setAction(AssistActivity.installedaction);
				// installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(installIntent);
				String pathString = msg.getData().getString("path");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(new File(pathString)),
						"application/vnd.android.package-archive");
				startActivity(intent);
				isInstalling = false;
				// stopForeground(true);// 取消前台服务
				NotificationManager mNotifyMgr = (NotificationManager) AssistApplication
						.getContext().getSystemService(NOTIFICATION_SERVICE);
				mNotifyMgr.cancel(2048);
				break;
			case CoreService.downloadfailed:
				Toast.makeText(AssistApplication.getContext(), "下载失败！",
						Toast.LENGTH_LONG).show();
				isInstalling = false;
				// stopForeground(true);// 取消前台服务
				NotificationManager motifyMgr = (NotificationManager) AssistApplication
						.getContext().getSystemService(NOTIFICATION_SERVICE);
				motifyMgr.cancel(2048);
				break;
			case CoreService.sendBroadcast:
				intentStrings = msg.getData().getStringArrayList("actions");
				freq = msg.getData().getInt("freq");
				sendBroadcastSimulation();
				break;
			case CoreService.stopsendBroadcast:
				stopSendBroadcast();
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
	public ButtonBroadcastReceiver bReceiver;

	@Override
	public void onCreate() {
		// if (intentStrings != null && intentStrings.isEmpty()) {
		// try {
		// InputStream inputStream = getAssets().open("build_in_actions");
		// int cnt = inputStream.available();
		// byte buf[] = new byte[cnt];
		// inputStream.read(buf);
		// String actions = new String(buf);
		// String actionsArrString[] = actions.split("\r\n");// note it may
		// // be
		// // different
		// // in linux
		// // system
		// intentStrings.addAll(Arrays.asList(actionsArrString));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		registerReceiver(bReceiver, intentFilter);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("study", "core service onstart");
	}

	private void init(String path) {
		SharedPreferences appdata = this.getSharedPreferences(
				getString(R.string.cfg_appdata), MODE_PRIVATE);
		appdata.edit().putString("obPath", path).commit(); // 防止被重启，把path保存到本地

	}

	public void startWatching() {
		SharedPreferences appdata = this.getSharedPreferences(
				getString(R.string.cfg_appdata), MODE_PRIVATE);
		String deafultpath = Environment.getExternalStorageDirectory()
				.getPath();
		deafultpath += File.separator + "MobileTool/CrashReport";
		observerpath = appdata.getString("obPath", deafultpath);
		emailReceiver = appdata.getString(getString(R.string.cfg_key_recevier),
				"pdatest@sogou-inc.com");
		if (listener != null) {
			listener.stopWatching();
			listener = null;
		}
		listener = new FileObserverThread(observerpath, emailReceiver);
		listener.startWatching();
	}

	public void stopWatching() {
		listener.stopWatching();
		listener = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("study", "core service onStartCommand");
		if (intent != null && intent.getBooleanExtra("setalarm", false)) {
			int cnt = getSharedPreferences(getString(R.string.cfg_action_cnt),
					MODE_PRIVATE).getInt(getString(R.string.key_action_cnt), 0);
			if (cnt > 0) {
				setNextAlarm(cnt);
			}
			return Service.START_STICKY;
		}

		createFloatView();
		SharedPreferences appdata = this.getSharedPreferences(
				getString(R.string.cfg_appdata), MODE_PRIVATE);

		int state = appdata.getInt("isWatching", AssistActivity.neverWatching);
		if (state == AssistActivity.isWatching) {
			startWatching();
		}

		setToolNotify();
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
		SharedPreferences appdata = getSharedPreferences(
				getString(R.string.cfg_appdata), MODE_PRIVATE);
		if (!appdata.getBoolean("isFloatWinOn", true)) {
			return;
		}
		if (btn_floatView != null) {
			wm.addView(btn_floatView, params);
			return;
		}
		btn_floatView = LayoutInflater.from(this).inflate(R.layout.floatwin,
				null);
		smallview = (ImageButton) btn_floatView
				.findViewById(R.id.fwinsmallview);
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
		appmagBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(it);

			}

		});
		nexthour = (ImageButton) btn_floatView.findViewById(R.id.nexthour);
		nexthour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",
						Locale.CHINA);
				Calendar ca = Calendar.getInstance();
				ca.add(Calendar.HOUR_OF_DAY, 1);
				String nowDate = format.format(ca.getTime());
				int hour = ca.get(Calendar.HOUR_OF_DAY);
				String nextTime = String.valueOf(hour) + "5500";
				String cmd = "date -s  " + nowDate + "." + nextTime;
				UsefulClass.processCmd(cmd);

			}

		});
		nextday = (ImageButton) btn_floatView.findViewById(R.id.nextday);
		nextday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Calendar ca = Calendar.getInstance();
				ca.add(Calendar.DAY_OF_MONTH, 1);
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",
						Locale.CHINA);
				String nowTime = format.format(ca.getTime());
				UsefulClass.processCmd("date -s " + nowTime + ".000500");
			}
		});
		wifisetview = (ImageButton) btn_floatView.findViewById(R.id.wifiset);
		wifisetview.setOnClickListener(new OnClickListener() {
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
		Context ctx = AssistApplication.getContext();
		if (ctx == null) {
			Log.e("err", "context is null");
			return false;
		}
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
				.getSharedPreferences(ctx.getString(R.string.cfg_appdata),
						MODE_PRIVATE);
		String emailReceiver = appdata.getString(
				ctx.getString(R.string.cfg_key_recevier),
				"pdatest@sogou-inc.com");

		ConnectivityManager mConnectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo == null || !mNetworkInfo.isConnected()) {
			Toast.makeText(AssistApplication.getContext(), "网络貌似有问题哦，邮件发不出去",
					Toast.LENGTH_LONG).show();

			return false;
		}

		if (MailSender.sendTextMail(title, info, path,
				new String[] { emailReceiver })) {
			String emailReceivername = appdata.getString("name", "pdatest");
			Toast.makeText(ctx, "截图完毕，" + emailReceivername + "同学请静候邮件~",
					Toast.LENGTH_LONG).show();
			File tmp = new File(path);
			if (tmp.exists())
				tmp.delete();
		} else {
			Toast.makeText(ctx, "发送邮件异常，可能是读截图失败了", Toast.LENGTH_LONG).show();
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

	public void installmt() {
		if (isInstalling) {
			Toast.makeText(this, "正在下载，请不要重复点击", Toast.LENGTH_LONG).show();
			return;
		}

		Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.setClass(AssistApplication.getContext(),
				AssistActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent contentIntent = PendingIntent.getActivity(
				AssistApplication.getContext(), 0, notificationIntent, 0);
		// notif.setLatestEventInfo(context, contentTitle, contentText,
		// contentIntent);
		Builder builder = new NotificationCompat.Builder(
				AssistApplication.getContext());
		builder.setContentIntent(contentIntent).setAutoCancel(false)
				.setSmallIcon(R.drawable.ic_launcher).setOngoing(true)
				.setContentTitle("正在下载最新版助手测试包");
		// startForeground(1024, builder.build());
		NotificationManager mNotifyMgr = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(2048, builder.build());
		new Thread(new Runnable() {

			@Override
			public void run() {

				isInstalling = true;

				String root_urlString = getResources().getString(
						R.string.mt_download_dir_url);
				String downloadurlString = null;
				try {
					downloadurlString = FetchNewestMTApk
							.getDownloadUrl(root_urlString);
				} catch (IOException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}
				if (downloadurlString == null) {
					Message msgMessage = new Message();
					msgMessage.what = downloadfailed;
					fltwinhandler.sendMessage(msgMessage);
					return;
				}

				int idx = downloadurlString.lastIndexOf("/");
				String filenameString = downloadurlString.substring(idx + 1);
				File file = new File(mtpathString + filenameString);

				if (file.exists()) {
					file.delete();
					file = null;
				}
				UsefulClass.Download(downloadurlString, mtpathString
						+ filenameString);
				Message msg = new Message();

				msg.what = CoreService.installmt;
				Bundle bundle = new Bundle();
				bundle.putString("path", mtpathString + filenameString);
				msg.setData(bundle);
				fltwinhandler.sendMessage(msg);

			}
		}).start();
	}

	private static ArrayList<String> intentStrings = null;

	public void sendBroadcastSimulation() {
		int cnt = getSharedPreferences(getString(R.string.cfg_action_cnt),
				MODE_PRIVATE).getInt(getString(R.string.key_action_cnt), 0);
		if (cnt > 0) {
			return;
		}
		setNextAlarm(0);
	}

	public void stopSendBroadcast() {
		intentStrings = null;
		freq = 0;
		getSharedPreferences(getString(R.string.cfg_action_cnt), MODE_PRIVATE)
				.edit().putInt(getString(R.string.key_action_cnt), 0).commit();
		AssistApplication.getAppDataPreferences()
				.edit().putBoolean(getString(R.string.issending), false)
				.commit();
	}

	public void setNextAlarm(int nextidx) {
		if (intentStrings == null) {
			return;
		}
		PowerManager.WakeLock wakeLock = null;

		final PowerManager pm = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"send test broadcast");
		wakeLock.acquire();

		if (nextidx == intentStrings.size()) {
			stopSendBroadcast();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss", Locale.CHINA);
			AssistApplication.getAppDataPreferences()
					.edit().putString(getString(R.string.last_sended_time),
							dFormat.format(calendar.getTime())).commit();
			wakeLock.release();
			wakeLock = null;
			return;
		}
		String bString = intentStrings.get(nextidx);
		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (alarms == null) {
			stopSendBroadcast();
			wakeLock.release();
			wakeLock = null;
			return;
		}

		Intent intent = new Intent(SimuBroadcastReceiver.broadcastAction);
		intent.setClassName("com.sogou.mobiletoolassist",
				SimuBroadcastReceiver.class.getName());

		intent.putExtra("broadcastname", bString);

		PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_ONE_SHOT);
		alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ freq * 60 * 1000, pIntent);
		AssistApplication.getAppDataPreferences()
				.edit().putBoolean(getString(R.string.issending), true)
				.commit();
		wakeLock.release();
		wakeLock = null;
	}

	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	public final static String INTENT_BUTTONID_TAG = "ButtonId";

	private void setToolNotify() {
		if (Build.VERSION.SDK_INT > 13) {

			RemoteViews mRemoteViews = new RemoteViews(getPackageName(),
					R.layout.view_custom_button);

			mRemoteViews.setImageViewResource(R.id.custom_song_icon,
					R.drawable.sing_icon);
			// API3.0 以上的时候显示按钮，否则消失
			mRemoteViews.setTextViewText(R.id.notifyTitle,
					getString(R.string.notifyTitileText));
			mRemoteViews.setTextViewText(R.id.notifyContent,
					getString(R.string.author));

			// 点击的事件处理
			Intent buttonIntent = new Intent(ACTION_BUTTON);
			buttonIntent.putExtra(INTENT_BUTTONID_TAG, 3);
			PendingIntent intent_next = PendingIntent.getBroadcast(this, 3,
					buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next,
					intent_next);
			Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
			notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			notificationIntent.setClass(this, AssistActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

			PendingIntent enterAssist = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
			mRemoteViews.setOnClickPendingIntent(R.id.toolsNotif, enterAssist);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					this);
			builder.setContent(mRemoteViews);
			builder.setOngoing(true);
			Notification nf = builder.build();
			nf.icon = R.drawable.ic_launcher;
			startForeground(1024, nf);
		} else {
			Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
			notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			notificationIntent.setClass(this, AssistActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
			Builder builder = new NotificationCompat.Builder(this);
			builder.setContentIntent(contentIntent).setAutoCancel(false)
					.setSmallIcon(R.drawable.ic_launcher).setOngoing(true)
					.setContentTitle("点击进入测试助手").setContentText("zs");
			startForeground(1024, builder.build());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public class ButtonBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			//
			String action = intent.getAction();
			if (action.equals(ACTION_BUTTON)) {
				// 通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case 1:
					// Log.d("assist" , "上一首");
					// Toast.makeText(getApplicationContext(), "上一首",
					// Toast.LENGTH_SHORT).show();
					// break;
				case 2:
					// String play_status = "";
					//
					// //showButtonNotify();
					// Log.d("assist" , play_status);
					// Toast.makeText(getApplicationContext(), play_status,
					// Toast.LENGTH_SHORT).show();
					// break;
				case 3:
					new Thread(new Runnable() {
						@Override
						public void run() {
							Uri uri = RingtoneManager
									.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);// 系统自带提示音
							Ringtone rt = RingtoneManager.getRingtone(
									getApplicationContext(), uri);
							if (rt != null)
								rt.play();
							// TODO 我没用线程时会出现intent接收的android
							// runtime，有时间解一下，先用线程解决
							Looper.prepare();
							CoreService.ScreenShot();
							Looper.loop();

						}

					}).start();

					break;
				default:
					break;
				}
			}
		}
	}
}
