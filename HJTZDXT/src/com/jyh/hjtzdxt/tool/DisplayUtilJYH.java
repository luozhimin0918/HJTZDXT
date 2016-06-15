package com.jyh.hjtzdxt.tool;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtilJYH {

	/**
	 * 判断不同机器的不同分辨率，然后设置不同的devider
	 */
	@SuppressWarnings({ "deprecation" })
	public static int setDividerHeight(Context context) {
		// TODO Auto-generated method stub
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth();

		// 720×1184
		// 根据宽度width来判断，因为高度有点误差
		if (width >= 720 && width < 1000) {
			return 10;
		} else if (width >= 1080) {
			return 15;
		} else if (width > 480 && width < 720) {
			return 8;
		} else if (width == 480) {
			return 7;
		} else if (width < 480) {
			return 5;
		}

		return 10; // 以上条件都不符合，返回10（默认值）
	}

	public static float getDpi(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		//240*320 0.75
		//320*480 1.0
		//480*800 1.5
		//720*1280 2.0
		//1080*1920 3.0
		return metrics.density;
	}
	
	/**
	 * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率�?px(像素) 的单�?转成�?dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	
	
	public static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}
	
	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatuBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			// e1.printStackTrace();
		}
		return sbar;
	}
}
