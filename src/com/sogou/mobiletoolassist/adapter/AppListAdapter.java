package com.sogou.mobiletoolassist.adapter;

import java.util.ArrayList;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {
	private ArrayList<PackageInfo> pkgList = null;
	private Context ctx = null;
	private LayoutInflater lInflater = null;
	public AppListAdapter(){
		ctx = AssistApplication.getContext();
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		if (pkgList != null) {
			return pkgList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (pkgList != null || pkgList.size() > position) {
			return pkgList.get(position).applicationInfo.name;
		}
		return "";
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = lInflater.inflate(R.layout.app_info_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tView = (TextView) convertView.findViewById(R.id.app_info_item_id);
			convertView.setTag(viewHolder);
			
		}
		viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.tView.setText((String)getItem(position));
		viewHolder.tView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		return convertView;
	}

	public static class ViewHolder{
		public TextView tView = null;
	}
}
