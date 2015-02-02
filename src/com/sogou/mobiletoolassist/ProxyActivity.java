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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.util.ShellCommand;
import com.sogou.mobiletoolassist.util.StateValue;
import com.sogou.mobiletoolassist.util.UsefulClass;
import com.sogou.mobiletoolassist.R.layout;
import com.sogou.mobiletoolassist.util.ShellCommand.CommandResult;

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

		try {
			basedir = getBaseContext().getFilesDir().getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		copyfile("redsocks");
		copyfile("proxy.sh");
		copyfile("redirect.sh");

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.mainview);

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		CheckBoxPreference cb = (CheckBoxPreference) findPreference("isEnabled");
		String addrsum = settings.getString("proxyHost", "");
		String portsum = settings.getString("proxyPort", "");
		final EditTextPreference addredit = (EditTextPreference) this
				.findPreference("proxyHost");
		final EditTextPreference portedit = (EditTextPreference) this
				.findPreference("proxyPort");
		
		if (!"".equals(addrsum)) {
			addredit.setSummary("当前为：" + addrsum);
			
		}
		if (!"".equals(portsum)) {

			portedit.setSummary("当前为：" + portsum);
		}
//		EditText et = addredit.getEditText();
//		Editable ea = et.getText();
//		int ln = ea.length();
//		Selection.setSelection(ea, 0);//
		addredit.setOnPreferenceChangeListener(new EditTextPreference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				addredit.setSummary("当前为：" + (String) arg1);
				addredit.setText((String) arg1);
				return false;
			}

		});
		portedit.setOnPreferenceChangeListener(new EditTextPreference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference arg0, Object arg1) {
				portedit.setSummary("当前为：" + (String) arg1);
				portedit.setText((String) arg1);
				return false;
			}

		});
		// PackageInfo pi = null;
		// try {
		// pi = getPackageManager().getPackageInfo(getPackageName(), 0);
		// } catch (PackageManager.NameNotFoundException e) {}
		// findPreference("version").setSummary("TransProxy "+pi.versionName);

		cb.setOnPreferenceChangeListener(new CheckBoxPreference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Boolean ret = proxy((Boolean) newValue ? START : STOP);
				setenabled(checklistener());
				return ret;
			}
		});

		setenabled(checklistener());
	}

	public boolean proxy(int action) {
		Process p = UsefulClass.getRootProcess();
		if (action == START) { // start proxy
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());

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
			// Log.v("tproxy","proxy.sh start " + basedir + " "
			// +"type=" + proxy_type + " "
			// +"host=" + ipaddr + " "
			// +"port=" + port.trim() + " "
			// +"auth=" + auth + " "
			// +"user=" + user.trim() + " "
			// +"pass=*****"
			// +"domain=" + domain.trim());

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
//				r = cmd.su.runWaitFor(basedir + "/redirect.sh start "
//						+ proxy_type);
				int ret = UsefulClass.processCmd(basedir + "/redirect.sh start "
						+ proxy_type);//有些手机上用原来的执行方法总是失败，换个方式
//				if (!r.success()) {
//					Log.v("tproxy", "Error starting redirect.sh (" + r.stderr
//							+ ")");
//					cmd.sh.runWaitFor(basedir + "/proxy.sh stop " + basedir);
//					alert("Failed to start redirect.sh (" + r.stderr + ")",
//							null);
//					return false;
//				} else {
//					Log.v("tproxy", "Successfully ran redirect.sh start "
//							+ proxy_type);
//					return true;
//				}
				if(ret != StateValue.success){
					cmd.sh.runWaitFor(basedir + "/proxy.sh stop " + basedir);
					alert("Failed to start redirect.sh (" + r.stderr + ")",
							null);
					return false;
				}
				return true;
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
		File f = new File(of);

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

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
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
