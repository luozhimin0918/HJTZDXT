package com.jyh.hjtzdxt.socket;

import com.jyh.hjtzdxt.bean.Constant;


/**
 * 全局参数-区别于ConstantValue，ConstantValue中是常量，这里可以是变量（值可变）
 * @author Administrator
 *
 */
public class GlobalParams {

	/**
	 * 是否更新过
	 */
	public static boolean alreadyUpdated = false;
	/** Server: apk下载路径 */
	public static String apkURL = Constant.newBaseUrl+"/app/version.json";
	//local:本地版本
	public static int localVerCode = 3;
//	public static String localVerName = "3.0";
	//server:服务器版本
	public static int serverVerCode = 4;
//	public static String serverVerName = "4.0";
	/**
	 * 每次运行APP打开的欢迎界面（区别于向导界面）
	 */
	public static boolean first_welcome = true;
	public static int currServerBeanId = -1;  //当前服务器的序号（List集合中的序号:0,1,2..）标识当前服务器，下次重连时不选择当前服务器
	//------------重要数据提醒：默认true-------------
	public static boolean importantDataRemind = true;
	/** hq--HVListView--作用:记录用户x轴上滑动的距离，,.切换Stock时初始化为0 */
	public static int mOffset=0;
}
