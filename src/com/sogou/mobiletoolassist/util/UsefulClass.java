package com.sogou.mobiletoolassist.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.StreamReader;
import com.sogou.mobiletoolassist.AssistApplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class UsefulClass {
	// 判断某个服务是否在运行
	public static boolean isServiceRunning(Context mContext, String className) {

		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(100);
		if (serviceList != null && serviceList.isEmpty()) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasappnamedxxx(Context mContext, String xxx) {
		PackageManager pkgmgr = (PackageManager) mContext.getPackageManager();
		if (pkgmgr != null) {
			try {
				PackageInfo pkg = pkgmgr.getPackageInfo(xxx, 0);
				if (pkg != null) {
					return true;
				}
			} catch (NameNotFoundException e) {
				//
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String getDeviceInfo() {
		String info = "";
		info += "Device name:";
		info += Build.MODEL;
		return info;
	}

	public static String getZSPkgInfo() {
		String info = "";
		info += "versionName:";
		Context ctx = AssistApplication.getContext();
		if (ctx != null) {
			PackageManager pm = ctx.getPackageManager();
			try {
				PackageInfo appinfo = pm.getPackageInfo(
						"com.sogou.androidtool", 0);
				info += appinfo.versionName;
				info += "</br>";
				info += "versionCode:";
				info += appinfo.versionCode;
			} catch (NameNotFoundException e) {
				//
				e.printStackTrace();
			}
		}
		return info;
	}

	public static Process getRootProcess() {
		ProcessBuilder pb = new ProcessBuilder().redirectErrorStream(true)
				.command("su");
		Process p = null;
		try {
			p = pb.start();
		} catch (IOException e1) {
			//
			e1.printStackTrace();
		}
		return p;
	}

	public static int processCmd(String cmd) {
		int ret = StateValue.success;
		Process p = getRootProcess();
		if (p == null)
			return StateValue.unroot;
		// We must handle the result stream in another Thread first
		StreamReader stdoutReader = new StreamReader(p.getInputStream(),
				"utf-8");
		stdoutReader.start();

		OutputStream out = p.getOutputStream();
		try {
			out.write((cmd + "\n").getBytes("utf-8"));
			out.write(("exit" + "\n").getBytes("utf-8"));
			out.flush();

		} catch (IOException e) {
			ret = StateValue.cmdfailed;
			e.printStackTrace();

		}
		String result = stdoutReader.getResult();
		Log.i(AssistActivity.myTag, result);
		return ret;
	}

	public static int processCmdWithoutWait(String cmd) {
		int ret = StateValue.success;
		Process p = getRootProcess();
		if (p == null)
			return StateValue.unroot;
		// We must handle the result stream in another Thread first
		StreamReader stdoutReader = new StreamReader(p.getInputStream(),
				"utf-8");
		stdoutReader.start();

		OutputStream out = p.getOutputStream();
		try {
			out.write((cmd + "\n").getBytes("utf-8"));
			out.write(("exit" + "\n").getBytes("utf-8"));
			out.flush();

		} catch (IOException e) {
			ret = StateValue.cmdfailed;
			e.printStackTrace();

		}
		// String result = stdoutReader.getResult();
		// Log.i("scresult", result);
		return ret;
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public static String upZipFile(File zipFile, String folderPath)
			throws ZipException, IOException {
		// public static void upZipFile() throws Exception{
		ZipFile zfile = new ZipFile(zipFile);
		@SuppressWarnings("rawtypes")
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		String zippedname = "";
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d(AssistActivity.myTag, "ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d(AssistActivity.myTag, "str = " + dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			Log.d(AssistActivity.myTag, "ze.getName() = " + ze.getName());
			long curtime = System.currentTimeMillis();
			zippedname = folderPath + File.separator + String.valueOf(curtime)
					+ ".rc";
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					zippedname));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();

		return zippedname;
	}

	public static boolean Download(String durl, String path) {
		try {
			URL url = new URL(durl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(10 * 1000); // 超时时间
			connection.connect(); // 连接
			if (connection.getResponseCode() == 200) { // 返回的响应码200,是成功.
				File file = new File(path); // 这里我是手写了。建议大家用自带的类
				file.createNewFile();
				InputStream inputStream = connection.getInputStream();
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(); // 缓存
				byte[] buffer = new byte[1024 * 100];
				while (true) {
					int len = inputStream.read(buffer);
					// publishProgress(len);
					if (len == -1) {
						break; // 读取完
					}
					arrayOutputStream.write(buffer, 0, len); // 写入
				}
				arrayOutputStream.close();
				inputStream.close();

				byte[] data = arrayOutputStream.toByteArray();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(data); // 记得关闭输入流
				fileOutputStream.close();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;

	}
	public static void LogToFile(String content){
		File file = new File("/sdcard"+ File.separator+"mtAssistLog.txt");
		
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(file,
					true), "UTF-8");
			writer.write(content+"\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
