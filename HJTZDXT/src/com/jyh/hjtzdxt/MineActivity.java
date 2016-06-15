package com.jyh.hjtzdxt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.hjtzdxt.WebActivity.ischangeTitle;
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;
import com.jyh.hjtzdxt.tool.DisplayUtilJYH;
import com.jyh.hjtzdxt.tool.ImageDownLoader;
import com.jyh.hjtzdxt.tool.ImageDownLoader.AsyncImageLoaderListener;

public class MineActivity extends Activity {
	private WebView webView;
	private TextView tv;
	private TextView name;
	private ImageView img, logout;
	private ImageView typeImg;
	private TextView typeName;
	private SharedPreferences preferences, appinfo;
	private SharedPreferences p;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		p = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
			this.setTheme(R.style.BrowserThemeDefault);
		setContentView(R.layout.activity_mine);

		setDialogStyle();

		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL); // ...but
																									// notify
																									// us
																									// that
																									// it
																									// happened.
		getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		// Note that flag changes
		// must happen *before* the
		// content view is set.
		preferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		appinfo = getSharedPreferences("appinfo", Context.MODE_PRIVATE);
		name = (TextView) findViewById(R.id.name);
		img = (ImageView) findViewById(R.id.img);
		typeImg = (ImageView) findViewById(R.id.typeImg);
		typeName = (TextView) findViewById(R.id.typeName);
		logout = (ImageView) findViewById(R.id.logoutId);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 退出登录
				Editor editor = preferences.edit();
				editor.putString("token", null);
				editor.putString("expired_time", null);
				editor.putString("login_uid", preferences.getString("uid", "-46182724291"));
				editor.putString("login_rid", preferences.getString("rid", "1"));
				editor.putString("login_name", preferences.getString("name", "游客-n291h"));
				editor.putBoolean("isFirstWTG",true);

				SCDataSqlte dataSqlte = new SCDataSqlte(MineActivity.this);
				SQLiteDatabase db = dataSqlte.getReadableDatabase();
				Cursor cursor = db.rawQuery("select * from roomrole where id=?", new String[] {"1"});
				while (cursor.moveToNext()) {
					editor.putString("r_name", cursor.getString(cursor.getColumnIndex("name")));
					editor.putString("limit_chat_time", cursor.getString(cursor.getColumnIndex("limit_chat_time")));
					editor.putString("limit_colorbar_time", cursor.getString(cursor.getColumnIndex("limit_colorbar_time")));
					editor.putString("power_visit_room", cursor.getString(cursor.getColumnIndex("power_visit_room")));
					editor.putString("image", cursor.getString(cursor.getColumnIndex("image")));
				}
				cursor.close();

				editor.commit();
				if (appinfo.getString("require_login", null) != null && "1".equals(appinfo.getString("require_login", null))) {
					// 强制登录
					Intent LoginIntent = new Intent(MineActivity.this, Login_One.class);
					startActivity(LoginIntent);
					MineActivity.this.finish();
					if (null != MainActivity.main)
						MainActivity.main.finish();
					if (GotyeLiveActivity.live != null) {
						GotyeLiveActivity.live.finish();
					}
				} else {
					MineActivity.this.finish();
				}
			}
		});
		name.setText(preferences.getString("login_name", null));
		typeName.setText(preferences.getString("role_name", null));
		String imgString = getSharedPreferences("appinfo", Context.MODE_PRIVATE).getString("roleimg_url", null)
				+ preferences.getString("role_image", null);
		Bitmap bm = new ImageDownLoader(this).getBitmapCache(imgString);
		if (bm != null) {
			typeImg.setImageBitmap(bm);
		} else {
			new ImageDownLoader(this).loadImage(imgString, new AsyncImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap) {
					// TODO Auto-generated method stub
					typeImg.setImageBitmap(bitmap);
				}
			});
		}
	}

	private void setDialogStyle() {
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		float size = DisplayUtilJYH.getDpi(this);
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 1.0); // 高度设置为屏幕的1.0
		p.height = (int) ((d.getHeight() - (float) 40 * size - DisplayUtilJYH.getStatuBarHeight(this)) / 2.75 * 1.75); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度

		getWindow().setAttributes(p); // 设置生效
		getWindow().setGravity(Gravity.BOTTOM); // 设置居中
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			finish();
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void finish() {
		if (GotyeLiveActivity.live != null) {
			((ischangeTitle) (GotyeLiveActivity.live)).changeTitle();
		}
		super.finish();
	}
}
