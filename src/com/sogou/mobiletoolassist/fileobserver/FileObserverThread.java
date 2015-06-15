package com.sogou.mobiletoolassist.fileobserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipException;
import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.util.MailSender;
import com.sogou.mobiletoolassist.util.UsefulClass;
import org.apache.http.util.EncodingUtils;
import android.os.FileObserver;
import android.util.Log;

public class FileObserverThread extends FileObserver {
	private String observerpath = null;
	private String tmp = null;
	private String emailReceiver = null;

	public FileObserverThread(String path,String emailRec) {
		/*
		 * 这种构造方法是默认监听所有事件的,如果使用super(String,int)这种构造方法， 则int参数是要监听的事件类型.
		 */
		super(path);
		observerpath = path;
		emailReceiver = emailRec;
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
						attach = UsefulClass.upZipFile(shot, observerpath);
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
					if (attach.length()!=0 && res.length()!=0) {

						MailSender.sendTextMail("手机助手崩溃文件监控",res, attach,
								new String[] { emailReceiver});
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

	
}