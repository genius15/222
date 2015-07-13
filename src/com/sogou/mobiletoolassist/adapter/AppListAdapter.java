package com.sogou.mobiletoolassist.adapter;

import java.util.ArrayList;
import java.util.List;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.setting.ConfirmTestAppActivity;
import com.sogou.mobiletoolassist.setting.GlobalSetting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter {
	private ArrayList<ApplicationInfo> pkgList = null;
	private Context ctx = null;
	private LayoutInflater lInflater = null;
	public AppListAdapter(ArrayList<ApplicationInfo> userapps){
		ctx = AssistApplication.getContext();
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.pkgList = userapps;
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
			
			return pkgList.get(position).loadLabel(ctx.getPackageManager());
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
		viewHolder.tView.setTag(pkgList.get(position));
		viewHolder.tView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ApplicationInfo app = (ApplicationInfo) v.getTag();
				Intent intent = new Intent(ctx,ConfirmTestAppActivity.class);
				intent.putExtra("appname", ((TextView)v).getText());
				intent.putExtra("pkgname", app.packageName);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(intent);
				
			}
		});
		return convertView;
	}

	public static class ViewHolder{
		public TextView tView = null;
	}
}
