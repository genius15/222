package com.sogou.mobiletoolassist.util;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;
import com.sogou.mobiletoolassist.AssistApplication;
public class NetworkUtil {
	private static RequestQueue mQueue = null;
	
	public static RequestQueue getRequestQueue() {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(AssistApplication.getContext());
		}
		return mQueue;
	}
}
