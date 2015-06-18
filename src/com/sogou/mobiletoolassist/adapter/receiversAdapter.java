<<<<<<< HEAD
package com.sogou.mobiletoolassist.adapter;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.contact.contactInfoArray;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class receiversAdapter extends BaseExpandableListAdapter {
	private contactInfoArray peopleArray = null;
	private Context ctx = null;
	public void addData(contactInfoArray array) {
		peopleArray = array;
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
=======
package com.sogou.mobiletoolassist.adapter;

import com.sogou.mobiletoolassist.AssistActivity;
import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.contact.contactInfoArray;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class receiversAdapter extends BaseExpandableListAdapter {
	private contactInfoArray peopleArray = null;
	private Context ctx = null;
	public void addData(contactInfoArray array) {
		peopleArray = array;
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
>>>>>>> e4307c32419c563cb85236c6f20837556f0818d9
