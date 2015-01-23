package com.sogou.mobiletoolassist.util;

/**
 * ScreenShotFb.java
 * 版权所有(C) 2014
 * 创建者:cuiran 2014-4-3 下午4:55:23
 */

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

/**
 * FrameBuffer中获取Android屏幕截图 
 * @author cuiran
 * @version 1.0.0
 */
public class ScreenshotforGINGERBREAD_MR1 {

	private static final String TAG="ScreenShotFb";
	
	final static String FB0FILE1 = "/dev/graphics/fb0";

	static File fbFile;
	  //程序入口  
    public static  void shoot(){  
    	 try {  
    		 /************ 创建锁对象 ************/
    	        final Object lock = new Object();
    	        
    	    	synchronized (lock) {
    	    		long start=System.currentTimeMillis();
    	    		Bitmap bitmap=getScreenShotBitmap();
    	        	long end=System.currentTimeMillis();
    	        	Log.i(TAG, "getScreenShotBitmap time is :"+(end-start)+" ms");
    	        	String filePath= Environment.getExternalStorageDirectory()+"/s.png";
//    	        	String filePath= ConstantValue.ROOT_SDCARD_DIR+"/screens/"+System.currentTimeMillis()+".png";
    	        	ScreenshotforGINGERBREAD_MR1.savePic(bitmap,filePath);  
    	    	}
         }catch (Exception e) {  
         	Log.e(TAG, "Exception error",e);
         }  
    
    
    }  
	 //保存到sdcard  
	public static void savePic(Bitmap b,String strFileName){  
        FileOutputStream fos = null;  
        try {  
            fos = new FileOutputStream(strFileName);  
            if (null != fos)  
            {  
                b.compress(Bitmap.CompressFormat.PNG, 50, fos);  
                fos.flush();  
                fos.close();  
            }  
        } catch (FileNotFoundException e) {  
        	Log.e(TAG, "FileNotFoundException error",e);
        } catch (IOException e) {  
        	Log.e(TAG, "IOException error",e);
        }  
        
        Log.i(TAG, "savePic success");
    }  
	
	 public static void init(Activity activity){  
		
			try {
			
				DisplayMetrics dm = new DisplayMetrics();
				Display display = activity.getWindowManager().getDefaultDisplay();
				display.getMetrics(dm);
				screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
				screenHeight = dm.heightPixels; // 屏幕高（像素，如：800p）
				int pixelformat = display.getPixelFormat();
				PixelFormat localPixelFormat1 = new PixelFormat();
				PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
				int deepth = localPixelFormat1.bytesPerPixel;// 位深
				Log.i(TAG, "deepth="+deepth);
				piex = new byte[screenHeight * screenWidth*deepth] ;// 像素
				colors = new int[screenHeight * screenWidth];
				
				
				
			}catch(Exception e){
				Log.e(TAG, "Exception error",e);
			}
	 }
	 static DataInputStream dStream=null;
	 static byte[] piex= new byte[480*854];
	 static int[] colors =null;
	 static int screenWidth = 480;
	 static int screenHeight = 854;
	 
	public static synchronized Bitmap getScreenShotBitmap() {
		FileInputStream buf = null;
		try {
			ProcessBuilder pb = new ProcessBuilder().redirectErrorStream(true).command("su");
			Process p = null;
			try {
				p = pb.start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(p!=null)
				//p.waitFor();
			fbFile = new File(FB0FILE1);
			buf = new FileInputStream(fbFile);// 读取文件内容
			dStream=new DataInputStream(buf);
			dStream.readFully(piex);
			dStream.close();
			// 将rgb转为色值
			  for(int m=0;m<piex.length;m+=1)
	            {
//				  colors[i/2]= (int)0xff000000 +
//	                        (int) (((piex[i+1]) << (16))&0x00f80000)+
//	                        (int) (((piex[i+1]) << 13)&0x0000e000)+
//	                        (int) (((piex[i]) << 5)&0x00001A00)+
//	                        (int) (((piex[i]) << 3)&0x000000f8);
				  if (m % 4 == 0) {
					  int r = (piex[m] & 0xFF);
					  int g = (piex[m + 1] & 0xFF);
					  int b = (piex[m + 2] & 0xFF);
					  int a = (piex[m + 3] & 0xFF);
					  colors[m / 4] = (a << 24) + (b << 16) + (g << 8) + r;
					  }
	            }

	       // 得到屏幕bitmap
			return Bitmap.createBitmap(colors, screenWidth, screenHeight,
    					Bitmap.Config.ARGB_8888);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}  
		finally {
			if(buf!=null){
				try {
					buf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
  
}
