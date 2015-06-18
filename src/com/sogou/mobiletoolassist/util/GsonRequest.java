<<<<<<< HEAD
package com.sogou.mobiletoolassist.util;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

public class GsonRequest<T> extends Request<T> {

	public GsonRequest(int method, String url, Response.Listener<T> listener,ErrorListener errorlistener) {
		super(method, url, errorlistener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		
	}

}
=======
package com.sogou.mobiletoolassist.util;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

public class GsonRequest<T> extends Request<T> {

	public GsonRequest(int method, String url, Response.Listener<T> listener,ErrorListener errorlistener) {
		super(method, url, errorlistener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		
	}

}
>>>>>>> e4307c32419c563cb85236c6f20837556f0818d9
