package com.sogou.mobiletoolassist.sharefile;

import java.io.File;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.util.MailSender;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class SendFileToTheContact extends Activity {

	@Override
	public void onCreate(Bundle bdl) {
		super.onCreate(bdl);
		final String receiverString = AssistApplication.getEmailAddr();
		if (receiverString == null) {
			Toast.makeText(this, getString(R.string.contact_miss), Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = getIntent();
		String type = intent.getType();
		if (type.equals("text/plain")) {
			final String contentString = intent.getStringExtra(Intent.EXTRA_TEXT);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if (receiverString != null) {
						MailSender.sendTextMail(
								getString(R.string.anyfilesharetitle), contentString, "",
								new String[] { receiverString });
					}
					
				}
			}).start();
		}else {
			Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			final String path = uri.getPath();
			if (path == null) {
				finish();
			}
			String filename = new File(path).getName();

			
			Toast.makeText(this,
					String.format(getString(R.string.ttsendfile), filename,receiverString),
					Toast.LENGTH_LONG).show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					if (receiverString != null) {
						MailSender.sendTextMail(
								getString(R.string.anyfilesharetitle), "", path,
								new String[] { receiverString });
					}

				}
			}).start();
		}
		
		
		finish();
	}

}
