package com.jyh.hjtzdxt;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jyh.hjtzdxt.bean.KXTApplication;

public class AnimtionActivity extends Activity {
	private KXTApplication application;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dh);
		application=(KXTApplication) getApplication();
		application.addAct(this);
		Intent intent = new Intent(AnimtionActivity.this, MainActivity.class);
		intent.putExtra("enter", "enter");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}
}
