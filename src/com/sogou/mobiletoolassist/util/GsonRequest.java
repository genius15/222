package com.sogou.mobiletoolassist.util;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonRequest<T> extends Request<T> {
	private Response.Listener<T> mListener = null;
	private Gson mGson = null;
	private Class<T> clazz;
	private Map<String, String> params = null;
	public GsonRequest(int method, String url, Class<T> clazz,
			Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		mGson = new GsonBuilder().create();
		this.clazz = clazz;
	}
	
	public GsonRequest(int method, String url, Class<T> clazz,
			Listener<T> listener, ErrorListener errorListener,Map<String, String> params) {
		super(method, url, errorListener);
		mListener = listener;
		mGson = new GsonBuilder().create();
		this.clazz = clazz;
		this.params = params;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		final String data = new String(response.data);
		if (clazz == null || mListener == null || mGson == null) {
			return null;
		}
		return Response.success(mGson.fromJson(data, clazz),
				HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(T response) {
		if (mListener != null || response != null) {
			mListener.onResponse(response);
		}
		
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError{
		return params;
	}

}
