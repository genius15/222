package com.sogou.mobiletoolassist.sharefile;

import java.io.File;

import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.util.MailSender;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SendFileToTheContact extends Activity {

	@Override
	public void onCreate(Bundle bdl) {
		
		Intent intent = getIntent();
		Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		final String path = uri.getPath();
		String filename = new File(path).getName();

		Log.i("sendfile", path);
		final String receiverString = getSharedPreferences(
				getString(R.string.cfgmailreceiver), MODE_PRIVATE)
				.getString(getString(R.string.cfg_key_recevier), null);
		Toast.makeText(this,
				String.format(getString(R.string.ttsendfile), filename),
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
		super.onCreate(bdl);
		finish();
	}

}
