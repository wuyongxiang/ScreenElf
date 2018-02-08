package com.xiangzi.screenelf.Util;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.xiangzi.screenelf.PetElf;


public class ElfUtil {
	
	public static ActivityManager mActivityManager;
	/** 
	 * 获得属于桌面的应用的应用包名称 
	 * @return 返回包含所有包名的字符串列表 
	 */
	public static List<String> getHomes(Context context) {
		List<String> names = new ArrayList<String>();  
		PackageManager packageManager = context.getPackageManager();  
		// 属性  
		Intent intent = new Intent(Intent.ACTION_MAIN);  
		intent.addCategory(Intent.CATEGORY_HOME);  
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,  
				PackageManager.MATCH_DEFAULT_ONLY);  
		for(ResolveInfo ri : resolveInfo) {  
			names.add(ri.activityInfo.packageName);  
		}
		return names;  
	}

	/** 
	 * 判断当前界面是否是桌面 
	 */  
	public static boolean isHome(Context context){  
		if(mActivityManager == null) {
			mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
		}
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);  
		return getHomes(context).contains(rti.get(0).topActivity.getPackageName());  
	}

	public static float getV (float x){
		return (float) (Math.sqrt(PetElf.k/PetElf.m)*x*x);
	}
}
