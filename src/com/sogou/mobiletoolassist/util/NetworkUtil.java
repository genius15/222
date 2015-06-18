<<<<<<< HEAD
package com.sogou.mobiletoolassist.util;
import com.android.volley.*;
import com.android.volley.Request.Method;
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
	
	public static <T> void get(String url,Class<T> clazz,Response.Listener<T> listener,
			Response.ErrorListener errorListener) {
		GsonRequest<T> request = new GsonRequest<T>(Method.GET, url, listener, errorListener);
		getRequestQueue().add(request);
	}
}
=======
package com.sogou.mobiletoolassist.util;
import com.android.volley.*;
import com.android.volley.Request.Method;
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
	
	public static <T> void get(String url,Class<T> clazz,Response.Listener<T> listener,
			Response.ErrorListener errorListener) {
		GsonRequest<T> request = new GsonRequest<T>(Method.GET, url, listener, errorListener);
		getRequestQueue().add(request);
	}
}
>>>>>>> e4307c32419c563cb85236c6f20837556f0818d9
