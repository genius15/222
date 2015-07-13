package com.sogou.mobiletoolassist.setting;

import java.util.ArrayList;
import java.util.List;

import com.sogou.mobiletoolassist.R;
import com.sogou.mobiletoolassist.adapter.ApkListAdapter;
import com.sogou.mobiletoolassist.adapter.AppListAdapter;
import com.sogou.mobiletoolassist.adapter.AppManagerAdapter;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class TestedAppSetting extends FragmentActivity implements LoaderCallbacks<Integer>{
	
	private ViewPager viewPager;
	private List<View> lists = new ArrayList<View>();
	private AppManagerAdapter adapter = null;
	private AppListAdapter installedAppAdapter = null;
	private ApkListAdapter apkAdapter = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_manager);

		// 热门商圈和热门分类 页面添加到viewPager集合
		ListView installedLV = new ListView(this);
		PackageManager pManager = this.getPackageManager();
		List<ApplicationInfo> packageInfos = pManager.getInstalledApplications(0);
		ArrayList<ApplicationInfo> userapps = new ArrayList<>();
		for (ApplicationInfo app : packageInfos) {
			if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				userapps.add(app);
			}
		}
		
		installedAppAdapter = new AppListAdapter(userapps);
		installedLV.setAdapter(installedAppAdapter);
		
		ListView uninstallLV = new ListView(this);
		apkAdapter = new ApkListAdapter();
		uninstallLV.setAdapter(apkAdapter);
		
		lists.add(installedLV);
		lists.add(uninstallLV);
		// 初始化滑动图片位置
		//initeCursor();
		adapter = new AppManagerAdapter(lists);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(adapter);
		// ViewPager滑动监听器
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				// 当滑动时，顶部的imageView是通过animation缓慢的滑动
//				switch (arg0) {
//				case 0:
//					if (currentItem == 1) {
//						animation = new TranslateAnimation(offSet * 2 + bmWidth, 0, 0,0);
//					} else if (currentItem == 2) {
//						animation = new TranslateAnimation(offSet * 4 + 2 * bmWidth, 0,0, 0);
//					}
//					
//					break;
//				case 1:
//					if (currentItem == 0) {
//						animation = new TranslateAnimation(0, offSet * 2 + bmWidth, 0,0);
//					} else if (currentItem == 2) {
//						animation = new TranslateAnimation(4 * offSet + 2 * bmWidth,offSet * 2 + bmWidth, 0, 0);
//					}
//					
//					
//					break;
//				}
//				currentItem = arg0;
//				animation.setDuration(500);
//				animation.setFillAfter(true);
//				imageView.startAnimation(animation);

			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		getSupportLoaderManager().initLoader(888, null, this);
	}

	@Override
	public Loader<Integer> onCreateLoader(int arg0, Bundle arg1) {
		
		return new ApkInfoLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<Integer> arg0, Integer arg1) {
		ApkInfoLoader loader = (ApkInfoLoader)arg0;
		apkAdapter.setData(loader.getAppInfos());
	}

	@Override
	public void onLoaderReset(Loader<Integer> arg0) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * 计算滑动的图片的位置
	 */
//	private void initeCursor() {
//		cursor = BitmapFactory.decodeResource(getResources(),R.drawable.viewpager_img);
//		bmWidth = cursor.getWidth();
//		DisplayMetrics dm;
//		dm = getResources().getDisplayMetrics();
//		offSet = (dm.widthPixels - 2 * bmWidth) / 4;
//		matrix.setTranslate(offSet, 0);
//		imageView.setImageMatrix(matrix); // 需要iamgeView的scaleType为matrix
//		currentItem = 0;
//	}

}
