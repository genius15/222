
package com.sogou.mobiletoolassist.adapter;


import java.util.ArrayList;
import java.util.HashMap;

import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.contact.ContactInfo;
import com.sogou.mobiletoolassist.ui.ContactInfoActivity;


import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContactAdapter extends BaseExpandableListAdapter {
	private HashMap<String, ArrayList<ContactInfo>> userMap = null;
	private Context ctx = null;
	private String[] names = null;
	private LayoutInflater mInflater = null;
	public void addData(HashMap<String, ArrayList<ContactInfo>> desktopqa) {
		userMap = desktopqa;
		names = new String[userMap.size()];
		names = userMap.keySet().toArray(names);
		notifyDataSetChanged();
	}
	public void clearData(){
		userMap = null;
		names = null;
		notifyDataSetChanged();
	}
	public ContactAdapter(Context ctx) {
		this.ctx = ctx;
		mInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupid, int childid) {
		@SuppressWarnings("unchecked")
		ArrayList<ContactInfo> infos = (ArrayList<ContactInfo>) getGroup(groupid);
		if (infos != null) {
			return infos.get(childid);
		}
		return null;
	}

	@Override
	public long getChildId(int groupid, int childid) {

		return childid;
	}

	@Override
	public View getChildView(int groupPos, int childPos, boolean islastChild,
			View convertView, ViewGroup parent) {
		UserViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.usertv, null);	
			viewHolder = new UserViewHolder();
			viewHolder.tvView = (TextView) convertView.findViewById(R.id.user_tv);
			
			viewHolder.tvView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					ContactInfo info = (ContactInfo) v.getTag();
					
					Intent intent = new Intent(ctx, ContactInfoActivity.class);
					intent.putExtra("name", info.name);
					intent.putExtra("email", info.email);
					intent.putExtra("ip", info.ip);
					ctx.startActivity(intent);
					//Toast.makeText(ctx, String.valueOf(info.name), Toast.LENGTH_LONG).show();
				}
			});
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (UserViewHolder) convertView.getTag();
		}
		ContactInfo user = (ContactInfo) getChild(groupPos, childPos);
		if (user != null) {
			viewHolder.tvView.setText(user.name);
			viewHolder.tvView.setTag(user);
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		@SuppressWarnings("unchecked")
		ArrayList<ContactInfo> infos = (ArrayList<ContactInfo>) getGroup(groupPosition);
		if (infos != null) {
			return infos.size();
		}
		
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (names != null) {
			return userMap.get(names[groupPosition]);
		}
		return null;
	}

	@Override
	public int getGroupCount() {
		if (userMap != null) {
			return userMap.size();
		}
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.group_tview, null);
			viewHolder = new GroupViewHolder();
			viewHolder.tView = (TextView) convertView.findViewById(R.id.group_tv);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (GroupViewHolder) convertView.getTag();
		}
		viewHolder.tView.setText(names[groupPos]);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {

		return true;
	}
	
	private static class GroupViewHolder{
		public TextView tView;
	}
	
	private static class UserViewHolder{
		public TextView tvView;
	}
}

