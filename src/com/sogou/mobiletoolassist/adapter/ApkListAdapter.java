package com.sogou.mobiletoolassist.adapter;

import java.util.ArrayList;
import java.util.List;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.adapter.AppListAdapter.ViewHolder;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ApkListAdapter extends BaseAdapter {
	private ArrayList<PackageInfo> packages = null;
	private LayoutInflater inflater = null;
	private Context context = null;
	private PackageManager pm = null;
	public ApkListAdapter() {
		context = AssistApplication.getContext();
		inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		pm = context.getPackageManager();
	}
	@Override
	public int getCount() {
		if (packages != null) {
			return packages.size();
		}
		return 0;
	}

	public void setData(List<PackageInfo> infos) {
		packages = (ArrayList<PackageInfo>) infos;
		notifyDataSetChanged();
	}
	@Override
	public Object getItem(int position) {
		if (packages != null) {
			PackageInfo info =  packages.get(position);
			if (info!= null) {
				
				return info.applicationInfo.loadLabel(pm);
			}
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.apk_info_item, null);
			holder = new viewHolder();
			holder.tv= (TextView) convertView.findViewById(R.id.apk_info_item_id);
			convertView.setTag(holder);
		}
		holder = (viewHolder) convertView.getTag();
		holder.tv.setText((String)getItem(position));
		holder.tv.setTag(packages.get(position));
		holder.tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		return convertView;
	}

	private static class viewHolder{
		TextView tv;
	}
}
