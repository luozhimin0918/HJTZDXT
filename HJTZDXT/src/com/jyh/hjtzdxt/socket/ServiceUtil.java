package com.jyh.hjtzdxt.socket;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceUtil {

	/**
	 * 判断一个服务是否在后台运行
	 * 
	 * @param context
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		if (null != mContext) {
			ActivityManager activityManager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> serviceList = activityManager
					.getRunningServices(400);
			if (null == serviceList || !(serviceList.size() > 0)) {
				return false;
			}
			if (serviceList.contains(className)) {
				isRunning = true;
			}
			return isRunning;
		}
		return false;
	}
}
