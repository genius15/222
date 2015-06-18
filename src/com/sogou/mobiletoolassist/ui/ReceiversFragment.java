package com.sogou.mobiletoolassist.ui;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.adapter.receiversAdapter;
import com.sogou.mobiletoolassist.contact.contactInfoArray;
import com.sogou.mobiletoolassist.util.NetworkUtil;

import android.app.DownloadManager.Request;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ReceiversFragment extends Fragment implements
		Response.Listener<contactInfoArray>, Response.ErrorListener {
	private receiversAdapter recAdapter = null;
	private ExpandableListView listv = null;
	private static String urlString = "http://venus.sogou-inc.com/anonymous/call/userInfoQuery.getUsersByIds()";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("learn", "rec onCreateView");
		listv = (ExpandableListView) inflater.inflate(R.layout.receivers,
				container, false);
		recAdapter = new receiversAdapter(getActivity());
		listv.setAdapter(recAdapter);
		return listv;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("learn", "rec onCreate");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		request();
	}
	
	private void request(){
		
		NetworkUtil.get(urlString, contactInfoArray.class, this, this);
	}
	
	@Override
	public void onResponse(contactInfoArray response) {
		if (response != null && !response.isEmpty()) {
			recAdapter.addData(response);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		Log.e(AssistActivity.myTag, error.getLocalizedMessage());
		
	}

}
