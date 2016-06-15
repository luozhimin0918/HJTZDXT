package com.jyh.hjtzdxt.socket;

/**
 * 接收行情推送数据回调的接口
 * @author Administrator
 *
 */
public interface IHqDataCallBack {

	/**
	 * 收到行情推送数据
	 * @param stockStr 
	 */
	void dataCome(String stockStr);
	
	/**
	 * 行情推送的数据
	 * @param pushStr
	 */
	void hqPushData(String pushStr);

	/**
	 * 行情配置文件xml
	 * @param configStr
	 */
	void hqConfigFile(String configStr);
}
