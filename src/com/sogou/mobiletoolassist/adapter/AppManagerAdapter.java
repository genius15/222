package com.sogou.mobiletoolassist.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class AppManagerAdapter extends PagerAdapter {

	private List<View> viewLists;
	private static ArrayList<String> titles = new ArrayList<>();
	static {
		titles.add("已安装应用");
		titles.add("未安装应用");
	}
	public AppManagerAdapter(List<View> lists) {
		viewLists = lists;
	}

	@Override
	public int getCount() { // 获得size
		// TODO Auto-generated method stub
		return viewLists.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) // 销毁Item
	{
		((ViewPager) view).removeView(viewLists.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) // 实例化Item
	{
		((ViewPager) view).addView(viewLists.get(position), 0);

		return viewLists.get(position);
	}

	@Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
