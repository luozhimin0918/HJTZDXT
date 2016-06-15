package com.jyh.hjtzdxt.receiver;

import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.tool.FaceConversionUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class LoadedImgReceiver extends BroadcastReceiver {

	public LoadedImgReceiver() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub

		final int emojiNum = KXTApplication.emojiNum;
		KXTApplication.isLoadedImg = true;
//		Toast.makeText(context, "表情包加载完毕", 0).show();
	}

}
