package com.sogou.mobiletoolassit.infostatic;

import java.util.HashMap;
import java.util.Map;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.util.NetworkUtil;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class Pingbackhandler {
	private static String pingback = "http://venus.sogou-inc.com/anonymous/call/toolFactory.postToolInfo";
	//private static String macaddr = null;

	public static void sendPB(String func,String duration) {
//		if (macaddr == null) {
//			Context context = AssistApplication.getContext();
//			WifiManager wifi = (WifiManager) context
//					.getSystemService(Context.WIFI_SERVICE);
//			WifiInfo info = wifi.getConnectionInfo();
//			macaddr = info.getMacAddress();
//		}
		if (duration == null || duration.isEmpty()) {
			duration = "1";
		}
		if (func == null || func.isEmpty()) {
			func = "Doraemon";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("toolName", "Doraemon");
		params.put("authorName", "zhangshuai203407");
		params.put("macType", "android api " + Build.VERSION.SDK_INT);
		params.put("reduceTime", duration);
		params.put("function", func);
		NetworkUtil.post(pingback, null, null, null, params);
	}
}
