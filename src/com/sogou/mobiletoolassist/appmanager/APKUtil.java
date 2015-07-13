package com.sogou.mobiletoolassist.appmanager;

import java.io.File;
import java.util.ArrayList;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

public class APKUtil {
	public static ArrayList<PackageInfo> getAllUninstalledAPKs() {
		File rootdir = Environment.getExternalStorageDirectory();

		return findAPKs(rootdir, AssistApplication.getContext()
				.getPackageManager());
	}

	public static ArrayList<PackageInfo> findAPKs(File dir, PackageManager pm) {
		ArrayList<PackageInfo> results = new ArrayList<>();
		File[] files = dir.listFiles();
		if (files == null) {
			return null;
		}
		for (File afile : files) {
			if (afile.isDirectory()) {
				ArrayList<PackageInfo> ret = findAPKs(afile, pm);
				if (ret != null) {
					results.addAll(ret);
				}
			} else {

				String name = afile.getName();
				if (name != null && name.endsWith(".apk")) {
					PackageInfo info = pm.getPackageArchiveInfo(
							afile.getPath(), PackageManager.GET_ACTIVITIES);
					if (info != null
							&& !UsefulClass.hasappnamedxxx(
									AssistApplication.getContext(),
									info.packageName)) {
						results.add(info);
					}
				}
			}
		}
		return results;
	}
}
