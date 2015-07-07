package com.sogou.mobiletoolassit.infostatic;

import java.util.HashMap;
import java.util.Map;

import com.sogou.mobiletoolassist.util.NetworkUtil;

import android.os.Build;

public class Pingbackhandler {
	private static String pingback = "http://venus.sogou-inc.com/anonymous/call/toolFactory.postToolInfo";
	
	public static void sendPB(String duration) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("toolName", "Doraemon");
		params.put("authorName", "zhangshuai203407");
		params.put("macType", "android api " + Build.VERSION.SDK_INT);
		if (duration != null && !duration.isEmpty()) {
			params.put("reduceTime", duration);
		}else {
			params.put("reduceTime", "0");
		}
		NetworkUtil.post(pingback, null, null, null, params);
	}
}
