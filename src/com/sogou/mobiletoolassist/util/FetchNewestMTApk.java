package com.sogou.mobiletoolassist.util;

import java.io.IOException;

import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import android.content.Context;



public class FetchNewestMTApk {

	public static void GetNewestApk(Context ctx) {
		
	}

	public static String getDownloadUrl(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		String newestVer = "";
		long date = 0;
		Elements eles = doc.select("table");// 读到table表
		for (Element ele : eles) {// 别看这里是循环，其实就一个表
			Elements tmpeles = ele.select("tr");// 读取table表的所有行
			for (Element folder : tmpeles) {
				Elements imgs = folder.getElementsByTag("img");// 每一行应该只有一个img
				boolean over = false;
				if (imgs.size() > 0) {
					Attributes attrs = imgs.get(0).attributes();
					for (Attribute attr : attrs) {
						if (attr.getValue().contains("/icons/folder.gif")
								|| attr.getValue().contains(
										"/icons/unknown.gif")) {// 这里判断这行是有效的
							over = true;
							break;
						}
					}
				}
				if (over) {
					Elements tdsElements = folder.getElementsByTag("td");
					if (tdsElements.size() == 4) {
						Element generate_date = tdsElements.get(2);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date(generate_date.text().trim()));
						long tm = calendar.getTimeInMillis();
						if (tm > date) {// 遍历所有行找到时间最近的那个
							date = tm;
							newestVer = tdsElements.get(1).text().trim();
						}
					}
				}
			}
		}
		if (newestVer.equals("")) {
			return "";
		}
		if (newestVer.endsWith("apk")) {
			return url + newestVer;
		} else {
			return getDownloadUrl(url + newestVer);
		}
	}
}
