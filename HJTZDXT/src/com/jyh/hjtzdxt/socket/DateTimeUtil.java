package com.jyh.hjtzdxt.socket;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;


public class DateTimeUtil {


	private static SimpleDateFormat sdf_temp1 = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm", Locale.US); // yyyy-MM-dd HH:mm:ss
	/** yyyy-MM-dd HH:mm */
	private static SimpleDateFormat sdf_cjrl = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Locale.US); // yyyy-MM-dd HH:mm:ss
	/** 2014-08-21 11:16:09 */
	private static SimpleDateFormat sdf_loadMore = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.US); // yyyy-MM-dd HH:mm:ss


	/**
	 * 处理特定的时间毫秒值格式: \/Date(1402628700000)\/
	 * 
	 * @param time
	 * @return 2014/06/10 16:27
	 */
	public static String parseMillis(String time) {
		String sub_time = time.substring(6, 19);
		// long long_time = Long.getLong(sub_time).longValue();
		long long_time = Long.parseLong(sub_time); // 这个可以
		String res = sdf_temp1.format(new Date(long_time));
		return res;
	}
	public static String parseMillis2(String time) {
		// long long_time = Long.getLong(sub_time).longValue();
		long long_time = Long.parseLong(time); // 这个可以
		
		Date date = new Date(long_time);
		int minutes=date.getMinutes();
		return "["+date.getHours()+":"+(minutes<10?"0"+minutes:minutes)+"]";
	}
	
	/**
	 * cjrl
	 * 
	 * @param time
	 * @return 2014-08-05 17:46
	 */
	public static String parseMillis_Cjrl(String time) {
		String sub_time = time.substring(6, 19);
		long long_time = Long.parseLong(sub_time); // 这个可以
		String res = sdf_cjrl.format(new Date(long_time));
		return res;
	}

	/**
	 * 副本
	 * 
	 * @param time
	 * @return
	 */
	public static String parseMillis_Cjrl_0806(String time) {
		String sub_time = time.substring(6, 19);
		// Log.i(TAG, "time: "+sub_time);
		long long_time = Long.parseLong(sub_time); // 这个可以
		String res = sdf_cjrl.format(new Date(long_time));
		return res;
	}

	/**
	 * 加载更多使用的转换器
	 * 
	 * @return
	 */
	public static String parseMillis_4loadMore(String time) {
		String sub_time = time.substring(6, 19);
		// long long_time = Long.getLong(sub_time).longValue();
		long long_time = Long.parseLong(sub_time); // 这个可以
		String res = sdf_loadMore.format(new Date(long_time));
		return res;
	}
	/**
	 * 时间格式化
	 * 
	 * @param millis
	 * @return
	 */
	public static String DateStr(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		@SuppressWarnings("resource")
		Formatter ft = new Formatter(Locale.CHINA);
		return ft.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", cal).toString();
	}
}
