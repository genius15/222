package com.sogou.mobiletoolassist.ui;

import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.adapter.receiversAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class ReceiversFragment extends Fragment {
	private receiversAdapter recAdapter = null;
	private ExpandableListView listv = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("learn", "rec onCreateView");
		listv = (ExpandableListView) inflater.inflate(R.layout.receivers,
				container, false);
		recAdapter = new receiversAdapter();
		listv.setAdapter(recAdapter);
		return listv;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("learn", "rec onCreate");
	}

}
