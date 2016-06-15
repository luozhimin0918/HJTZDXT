package com.jyh.hjtzdxt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.hjtzdxt.sqlte.SCDataSqlte;

public class UserActivity extends Activity implements OnClickListener {
	private SharedPreferences preferences;
	private boolean isLogin;
	private LinearLayout changeName, changePwd, back, logout;
	private SharedPreferences userinfo, appinfo;
	private String name;
	private SCDataSqlte dataSqlte;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
			this.setTheme(R.style.BrowserThemeDefault);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		isLogin = getIntent().getBooleanExtra("isLogin", false);
		findViewById(R.id.user_login).setVisibility(View.VISIBLE);
		userinfo = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		appinfo = getSharedPreferences("appinfo", Context.MODE_PRIVATE);
		findView();
	}

	private void findView() {
		// TODO Auto-generated method stub
		changeName = (LinearLayout) findViewById(R.id.self_change_name);
		changePwd = (LinearLayout) findViewById(R.id.self_change_pwd);
		back = (LinearLayout) findViewById(R.id.user_back);
		logout = (LinearLayout) findViewById(R.id.self_ll_out);

		name = userinfo.getString("login_name", null);
		if (name != null)
			((TextView) findViewById(R.id.self_username)).setText(name);

		back.setOnClickListener(this);
		changeName.setOnClickListener(this);
		changePwd.setOnClickListener(this);
		logout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.self_change_name:
			Intent intent = new Intent(this, ChangeNameActivity.class);
			startActivityForResult(intent, 100);
			break;
		case R.id.self_change_pwd:
			startActivity(new Intent(this, ChangePwdActivity.class));
			break;
		case R.id.user_back:
			finish();
			break;
		case R.id.self_ll_out:
			Editor editor = userinfo.edit();
			editor.putString("token", null);
			editor.putString("expired_time", null);
			editor.putString("login_uid", userinfo.getString("uid", "-46182724291"));
			editor.putString("login_rid", userinfo.getString("rid", "1"));
			editor.putString("login_name", userinfo.getString("name", "游客-n291h"));
			editor.putBoolean("isFirstWTG",true);

			dataSqlte = new SCDataSqlte(UserActivity.this);
			SQLiteDatabase db = dataSqlte.getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from roomrole where id=?", new String[] { "1" });
			while (cursor.moveToNext()) {
				editor.putString("r_name", cursor.getString(cursor.getColumnIndex("name")));
				editor.putString("limit_chat_time", cursor.getString(cursor.getColumnIndex("limit_chat_time")));
				editor.putString("limit_colorbar_time", cursor.getString(cursor.getColumnIndex("limit_colorbar_time")));
				editor.putString("power_visit_room", cursor.getString(cursor.getColumnIndex("power_visit_room")));
				editor.putString("image", cursor.getString(cursor.getColumnIndex("image")));
			}
			cursor.close();

			editor.commit();
			if (appinfo.getString("require_login", null) != null && !"".equals(appinfo.getString("require_login", null))) {
				// 强制登录
				Intent LoginIntent = new Intent(this, Login_One.class);
				LoginIntent.putExtra("from", "self");
				startActivity(LoginIntent);
				if (MainActivity.main != null)
					MainActivity.main.finish();
				finish();
			} else
				finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == 666) {
			String name = userinfo.getString("login_name", null);
			if (name != null)
				((TextView) findViewById(R.id.self_username)).setText(name);
			setResult(666);
		}
	}
}
