package com.sogou.mobiletoolassist.fileobserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.util.MailSender;
import org.apache.http.util.EncodingUtils;

import android.os.FileObserver;
import android.util.Log;

public class FileObserverThread extends FileObserver {
	private String observerpath = null;
	private String tmp = null;

	public FileObserverThread(String path) {
		/*
		 * 这种构造方法是默认监听所有事件的,如果使用super(String,int)这种构造方法， 则int参数是要监听的事件类型.
		 */
		super(path);
		observerpath = path;
	}

	@Override
	public void onEvent(int event, String path) {
		switch (event) {
		case FileObserver.CREATE:
			Log.d(AssistActivity.myTag, "path:" + path);
			System.out.println("everything");
			break;
		case FileObserver.CLOSE_WRITE:
			if (!path.endsWith("zip")) {
				break;
			}
			tmp = observerpath + File.separator + path;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					String attach = "";
					String res = "";
					try {
						File shot = new File(tmp);
						attach = upZipFile(shot, observerpath);
						FileInputStream fin = new FileInputStream(attach);
						int length = fin.available();
						byte[] buffer = new byte[length];
						fin.read(buffer);
						res = EncodingUtils.getString(buffer, "UTF-8");
						fin.close();
					} catch (ZipException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					if (attach != "" && res != "") {

						MailSender.sendTextMail("手机助手崩溃文件监控",res, attach,
								new String[] { "pdatest@sogou-inc.com" });
						Log.i(AssistActivity.myTag, "send mail over:" + tmp);
						File att = new File(attach);
						if (att.exists()) {
							att.delete();
						}
					}

				}
			}).start();
			break;
		}
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
		Log.d(AssistActivity.myTag, "finishssssssssssssssssssss");
		return zippedname;
	}
}
