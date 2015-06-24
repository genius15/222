package com.sogou.mobiletoolassist.adapter;

import java.util.ArrayList;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ActionsAdapter extends BaseAdapter {
	private ArrayList<String> mActionsList = null;
	private Context ctx = null;
	LayoutInflater lInflater = null;
	public ActionsAdapter(ArrayList<String> actions) {
		mActionsList = actions;
		ctx = AssistApplication.getContext();
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (mActionsList != null) {
			return mActionsList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mActionsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (mActionsList == null) {
			return null;
		}
		String itemContentString = mActionsList.get(position);
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = lInflater.inflate(R.layout.action_item, null);
			holder = new ViewHolder();
			holder.tView = (TextView) convertView.findViewById(R.id.actiontv);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		int sendedcnt = ctx.getSharedPreferences(
				ctx.getString(R.string.cfg_action_cnt), Context.MODE_PRIVATE)
				.getInt(ctx.getString(R.string.key_action_cnt), 0);
		if (position < sendedcnt) {
			holder.tView.setTextColor(Color.GREEN);
		}
		holder.tView.setText(itemContentString);
		return convertView;
	}
	private static class ViewHolder{
		public TextView tView;
	}
}
