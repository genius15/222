package com.sogou.mobiletoolassist.util;

import android.annotation.SuppressLint;


public class ScreenshotforJELLY_BEAN {
	@SuppressLint("SdCardPath")
	private static String imagepath = "/sdcard/";//Environment.getExternalStorageDirectory()+File.separator;
	//这里一用环境变量获取得到的是storage/emulated/0/xxx.png然后读不到，不知道为啥，有时间研究一下
	public static String shoot(){
		
		long ti = System.currentTimeMillis();
		String path = imagepath + String.valueOf(ti)+".png";
		String cmd = "screencap "+path;
		if(UsefulClass.processCmd(cmd) != StateValue.success){			
			return "";
		}	
		return path;
	}
}
