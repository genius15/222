package com.sogou.mobiletoolassist;

import android.app.Activity;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.CheckBoxPreference;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.util.ShellCommand;
import com.sogou.mobiletoolassist.util.ShellCommand.CommandResult;
import com.sogou.mobiletoolassist.util.UsefulClass;

public class ProxyActivity extends PreferenceActivity {
	public static final String PREFS_NAME = "prefs";
	final int START = 1;
	final int STOP = 2;
	String basedir = null;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		File f = new File("/system/xbin/iptables");
		if (!f.exists()) {
			f = new File("/system/bin/iptables");
			if (!f.exists()) {
				alert("No iptables binary found on your ROM !", this);
			}
		}

		f = new File("/system/xbin/su");
		if (!f.exists()) {
			f = new File("/system/bin/su");
			if (!f.exists()) {
				alert("No su binary found on your ROM !", this);
			}
		}
		if(basedir == null){
			try {
				basedir = getBaseContext().getFilesDir().getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		copyfile("redsocks");
		copyfile("proxy.sh");
		copyfile("redirect.sh");
		copyTcpDumpfile();
		copyBusybox();
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.mainview);

		SharedPreferences settings = AssistApplication.getAppDataPreferences();
//				PreferenceManager
//				.getDefaultSharedPreferences(getBaseContext());

		CheckBoxPreference cb = (CheckBoxPreference) findPreference("isEnabled");
		String addrsum = settings.getString("proxyHost", "");
		String portsum = settings.getString("proxyPort", "");
		final EditTextPreference addredit = (EditTextPreference) this
				.findPreference("proxyHost");
		final EditTextPreference portedit = (EditTextPreference) this
				.findPreference("proxyPort");
		CheckBoxPreference tcpdumenable = (CheckBoxPreference) this
				.findPreference("isTcpdumpEnabled");
		if (!"".equals(addrsum)) {
			addredit.setSummary("ip:" + addrsum);

		}
		if (!"".equals(portsum)) {
			portedit.setSummary("port:" + portsum);
		}

		addredit.setOnPreferenceChangeListener(new EditTextPreference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				addredit.setSummary("ip:" + (String) arg1);
				addredit.setText((String) arg1);
				return false;
			}

		});
		portedit.setOnPreferenceChangeListener(new EditTextPreference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				portedit.setSummary("port:" + (String) arg1);
				portedit.setText((String) arg1);
				return false;
			}

		});

		cb.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Boolean ret = proxy((Boolean) newValue ? START : STOP);
				setenabled(checklistener());
				return ret;
			}
		});
		tcpdumenable.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if((Boolean) newValue){				
					UsefulClass.processCmdWithoutWait("/data/local/tcpdump -n -s 0 -w - | busybox nc -l -p 11233 ");
				}else{
					UsefulClass.processCmd("busybox killall tcpdump");
				}
				
				return true;
			}
		});
		setenabled(checklistener());
	}

	public boolean proxy(int action) {
		if (action == START) { // start proxy
			SharedPreferences settings = AssistApplication.getAppDataPreferences();
//					PreferenceManager
//					.getDefaultSharedPreferences(getBaseContext());

			String host = settings.getString("proxyHost", "");
			String port = settings.getString("proxyPort", "");
			Boolean auth = false;
			String user = "";
			String pass = "";
			String domain = "";
			String proxy_type = "http";

			String ipaddr;

			if (host.trim().equals("")) {
				alert("Hostname/IP is empty", null);
				return false;
			}
			if (port.trim().equals("")) {
				alert("Port is NULL", null);
				return false;
			}
			if (auth) {
				if (user.trim().equals("")) {
					alert("Auth is enabled but username is NULL", null);
					return false;
				}
				if (pass.trim().equals("")) {
					alert("Auth is enabled but password is NULL", null);
					return false;
				}
			}
			try {
				InetAddress addr = InetAddress.getByName(host.trim());
				ipaddr = addr.getHostAddress();
			} catch (UnknownHostException e) {
				alert("Cannot resolve hostname " + host, null);
				return false;
			}

			ShellCommand cmd = new ShellCommand();
			CommandResult r = cmd.sh.runWaitFor(basedir + "/proxy.sh start "
					+ basedir + " " + proxy_type + " " + ipaddr + " "
					+ port.trim() + " " + auth + " " + user.trim() + " "
					+ pass.trim() + " " + domain.trim());

			if (!r.success()) {
				Log.v("tproxy", "Error starting proxy.sh (" + r.stderr + ")");
				cmd.sh.runWaitFor(basedir + "/proxy.sh stop " + basedir);
				alert("Failed to start proxy.sh (" + r.stderr + ")", null);
				return false;
			}

			if (checklistener()) {
				r = cmd.su.runWaitFor(basedir + "/redirect.sh start "
						+ proxy_type);
				if (!r.success()) {
					Log.v("tproxy", "Error starting redirect.sh (" + r.stderr
							+ ")");
					cmd.sh.runWaitFor(basedir + "/proxy.sh stop " + basedir);
					alert("Failed to start redirect.sh (" + r.stderr + ")",
							null);
					return false;
				} else {
					Log.v("tproxy", "Successfully ran redirect.sh start "
							+ proxy_type);
					return true;
				}

			} else {
				alert("Proxy failed to start", null);
				return false;
			}
		} else { // stop tproxy
			Log.v("tproxy", "Successfully ran redirect.sh stop");
			ShellCommand cmd = new ShellCommand();
			cmd.sh.runWaitFor(basedir + "/proxy.sh stop " + basedir);
			cmd.su.runWaitFor(basedir + "/redirect.sh stop");
			return true;
		}
	}

	public void copyfile(String file) {
		String of = file;
		File f = new File(basedir+File.separator+of);

		if (!f.exists()) {
			try {
				InputStream in = getAssets().open(file);
				FileOutputStream out = getBaseContext().openFileOutput(of,
						MODE_PRIVATE);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
				Runtime.getRuntime().exec("chmod 700 " + basedir + "/" + of);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void copyTcpDumpfile() {
		try {
			UsefulClass.processCmd("chmod 777 /data");
			UsefulClass.processCmd("chmod 777 /data/local");
			File tcpdump = new File("/data/local/tcpdump");
			if (!tcpdump.exists()) {
				InputStream in = getAssets().open("tcpdump");
				FileOutputStream out = new FileOutputStream(
						"/data/local/tcpdump");

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
				Runtime.getRuntime().exec("chmod 777  /data/local/tcpdump");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void copyBusybox() {
		try {
			Runtime.getRuntime().exec("chmod 777 /system/bin");
			File busybox = new File("/system/bin/busybox");
			if (!busybox.exists()) {
				InputStream in = getAssets().open("busybox");
				FileOutputStream out = new FileOutputStream(
						"/system/bin/busybox");
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
				Runtime.getRuntime().exec("chmod 777  /system/bin/busybox");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void alert(String msg, Activity a) {

		final Activity act = a;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setCancelable(false)
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (act != null)
							act.finish();
						else
							dialog.cancel();
					}
				}).show();
	}

	public boolean checklistener() {
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", 8123);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (socket != null && socket.isConnected()) {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public void setenabled(boolean b) {

		SharedPreferences settings = AssistApplication.getAppDataPreferences();
//				PreferenceManager
//				.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = settings.edit();
		Log.v("tproxy", "Enabled = " + b);

		CheckBoxPreference cb = (CheckBoxPreference) findPreference("isEnabled");

		cb.setChecked(b);

		// findPreference("username").setEnabled(!b);
		// findPreference("password").setEnabled(!b);
		// findPreference("domain").setEnabled(!b);
		// findPreference("isAuthEnabled").setEnabled(!b);
		// findPreference("proxyType").setEnabled(!b);
		findPreference("proxyHost").setEnabled(!b);
		findPreference("proxyPort").setEnabled(!b);

		editor.putBoolean("isEnabled", b);
		editor.commit();
	}
}