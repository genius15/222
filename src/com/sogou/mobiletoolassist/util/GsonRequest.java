package com.sogou.mobiletoolassist.util;

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

	public GsonRequest(int method, String url, Class<T> clazz,
			Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
		mGson = new GsonBuilder().create();
		this.clazz = clazz;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		final String data = new String(response.data);

		return Response.success(mGson.fromJson(data, clazz),
				HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		mListener.onResponse(response);
	}

}
