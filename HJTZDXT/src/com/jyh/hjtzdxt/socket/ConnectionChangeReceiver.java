package com.jyh.hjtzdxt.socket;

import com.jyh.hjtzdxt.bean.KXTApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	private ConnectivityManager connectivityManager;
	private NetworkInfo info;
	private KXTApplication application;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				KXTApplication.isHaveNet=true;
				Intent intent1 = new Intent();
				intent1.setAction("newData");
				intent1.putExtra("mess", "isConn");
				context.sendBroadcast(intent1);// 发送广播，通知行情页面刷新
			} else {
				KXTApplication.isHaveNet=false;
				if (null != application && null != application.getMmp()) {
					if (application.getMmp().size() < 4
							&& application.getMmp().size() > 0) {
						Toast.makeText(application.getApplicationContext(),
								"网络连接断开，程序即将退出", Toast.LENGTH_SHORT).show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								application.exitAppAll();
							}
						}).start();
					}
				}
			}
		}
	}

}
