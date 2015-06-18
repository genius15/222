
package com.sogou.mobiletoolassist.adapter;


import java.util.ArrayList;
import java.util.HashMap;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.contact.contactInfo;
import com.sogou.mobiletoolassist.contact.contactInfoArray;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class receiversAdapter extends BaseExpandableListAdapter {
	private contactInfoArray peopleArray = null;
	private HashMap<String, ArrayList<contactInfo> > desktopqa = new HashMap<>();
	private Context ctx = null;
	public void addData(contactInfoArray array) {
		peopleArray = array;
		for (contactInfo contactInfo : peopleArray) {
			//desktopqa.containsKey(contactInfo.userGroupIds[0])
		}
		notifyDataSetChanged();
	}
	public receiversAdapter(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public Object getChild(int groupid, int childid) {
		if (groupid == 0) {
			return AssistActivity.names[childid];
		}
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {

		return arg1;
	}

	@Override
	public View getChildView(int groupPos, int childPos, boolean islastChild,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new TextView(AssistApplication.getContext());			
		}
		((TextView)convertView).setText((String) getChild(groupPos, childPos));
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition == 0) {
			return AssistActivity.names.length;
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {

		return 0;
	}

	@Override
	public int getGroupCount() {

		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new TextView(AssistApplication.getContext());			
		}
		((TextView)convertView).setText((String) getGroup(groupPos));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {

		return false;
	}

}

