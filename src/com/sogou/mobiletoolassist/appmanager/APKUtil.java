package com.sogou.mobiletoolassist.appmanager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import com.sogou.mobiletoolassist.AssistApplication;
import com.sogou.mobiletoolassist.setting.APKInfo;
import com.sogou.mobiletoolassist.util.StateValue;
import com.sogou.mobiletoolassist.util.UsefulClass;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public class APKUtil {
	private final static String PATH_PackageParser = "android.content.pm.PackageParser";
	private static final String PATH_AssetManager = "android.content.res.AssetManager";
	public static ArrayList<APKInfo> getAllUninstalledAPKs() {
		File rootdir = Environment.getExternalStorageDirectory();

		return findAPKs(rootdir, AssistApplication.getContext()
				.getPackageManager());
	}

	public static ArrayList<APKInfo> findAPKs(File dir, PackageManager pm) {
		ArrayList<APKInfo> results = new ArrayList<>();
		File[] files = dir.listFiles();
		if (files == null) {
			return null;
		}
		Context ctx = AssistApplication.getContext();
		for (File afile : files) {
			if (afile.isDirectory()) {
				ArrayList<APKInfo> ret = findAPKs(afile, pm);
				if (ret != null) {
					results.addAll(ret);
				}
			} else {

				String name = afile.getName();
				if (name != null && name.toLowerCase(Locale.US).endsWith(".apk")
						&& afile.canWrite()) {
					PackageInfo info = null;
					
					info = pm.getPackageArchiveInfo(
							afile.getPath(), PackageManager.GET_ACTIVITIES);
					
					
					if (info != null
							&& !UsefulClass.hasappnamedxxx(
									AssistApplication.getContext(),
									info.packageName)) {
						APKInfo apk = new APKInfo();
						apk.pkgInfo = info;
						apk.apkpath = afile.getAbsolutePath();
						apk.name = info.packageName;
						String nameString = getAPKName(ctx, apk.apkpath);
						if (nameString != null) {
							apk.name = nameString;
						}
						results.add(apk);
					}
					
				}
			}
		}
		return results;
	}
	
	private static String getAPKName(Context ctx,String apkPath) {
		try {
			
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			Class<?>[] typeArgs = { String.class };
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs = { apkPath };
			Object pkgParser = pkgParserCt.newInstance(valueArgs);

			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();// 这个是与显示有关的, 这边使用默认
			typeArgs = new Class<?>[] { File.class, String.class,
					DisplayMetrics.class, int.class };
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);

			valueArgs = new Object[] { new File(apkPath), apkPath, metrics, 0 };

			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);

			if (pkgParserPkg == null) {
				return null;
			}
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");

			if (appInfoFld.get(pkgParserPkg) == null) {
				return null;
			}
			ApplicationInfo appinfo = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);

			Class<?> assetMagCls = Class.forName(PATH_AssetManager);
			Object assetMag = assetMagCls.newInstance();
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);

			Resources res = ctx.getResources();
			typeArgs = new Class[3];
			typeArgs[0] = assetMag.getClass();
			typeArgs[1] = res.getDisplayMetrics().getClass();
			typeArgs[2] = res.getConfiguration().getClass();
			Constructor<Resources> resCt = Resources.class
					.getConstructor(typeArgs);
			valueArgs = new Object[3];
			valueArgs[0] = assetMag;
			valueArgs[1] = res.getDisplayMetrics();
			valueArgs[2] = res.getConfiguration();
			res = (Resources) resCt.newInstance(valueArgs);

			if (appinfo != null) {
				if (appinfo.labelRes != 0) {
					CharSequence name1 = (CharSequence) res
							.getText(appinfo.labelRes);//
					if (name1 != null) {
						return name1.toString();
					}
				} 

			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean installAPK(String pathString) {
		String installcmd = "pm install -r "+pathString;
		
		if (StateValue.success != UsefulClass.processCmd(installcmd)) {
			AssistApplication.ShowToast("快捷安装失败！");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(pathString)),
					"application/vnd.android.package-archive");
			AssistApplication.getContext().startActivity(intent);
		}
		
		
		return true;
	}
	public static void installAPKNotSlience(String pathString) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(pathString)),
				"application/vnd.android.package-archive");
		AssistApplication.getContext().startActivity(intent);
	}
	public static boolean uninstallAPP(String pkgString) {
		String cmd = "pm uninstall "+pkgString;
		if (UsefulClass.processCmd(cmd) != StateValue.success) {
			AssistApplication.ShowToast("快速卸载失败！");
			Uri packageUri = Uri.parse("package:" + pkgString);  
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(packageUri);
			AssistApplication.getContext().startActivity(intent);
		}
		return true;
	}
}
