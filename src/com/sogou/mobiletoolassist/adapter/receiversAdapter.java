package com.sogou.mobiletoolassist.adapter;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class receiversAdapter extends BaseExpandableListAdapter {
	private String[] teams;

	public receiversAdapter() {
		teams = AssistApplication.getContext().getResources()
				.getStringArray(R.array.teams);
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

		return teams[groupPosition];
	}

	@Override
	public int getGroupCount() {

		return teams.length;
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
