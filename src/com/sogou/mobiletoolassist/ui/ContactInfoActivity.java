package com.sogou.mobiletoolassist.ui;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContactInfoActivity extends Activity {
	private String name = null;
	private String email = null;
	private String ip = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info_activity);
		Intent intent = getIntent();
		if (intent != null) {
			name = intent.getStringExtra("name");
			email = intent.getStringExtra("email");
			ip = intent.getStringExtra("ip");
			TextView nameTextView = (TextView) findViewById(R.id.contact_name_content);
			nameTextView.setText(name);
			TextView emailtTextView = (TextView) findViewById(R.id.contact_email_content);
			emailtTextView.setText(email);
			TextView iptexTextView = (TextView) findViewById(R.id.contact_ip_content);
			iptexTextView.setText(ip);
		}
	}
	
	
	public void onSetitAsContact(View v){
		if (name != null && email != null) {
			AssistApplication.putEmailName(name);
			AssistApplication.putEmailAddr(email);
			AssistApplication.putIp(ip);
			AssistApplication.putPort("8888");
		}
		finish();
	}
	
	public void onContactCancel(View V){
		finish();
	}
}
