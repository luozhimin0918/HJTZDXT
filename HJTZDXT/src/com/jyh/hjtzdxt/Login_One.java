package com.jyh.hjtzdxt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.hjtzdxt.WebActivity.ischangeTitle;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.customtool.BounceTopEnter;
import com.jyh.hjtzdxt.customtool.NormalDialog;
import com.jyh.hjtzdxt.customtool.OnBtnClickL;
import com.jyh.hjtzdxt.customtool.SlideBottomExit;
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;
import com.jyh.hjtzdxt.tool.Code;
import com.jyh.hjtzdxt.tool.DisplayUtilJYH;
import com.jyh.hjtzdxt.tool.NormalPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 强制登录界面
 * 
 * @author Administrator
 *
 */
public class Login_One extends Activity implements OnClickListener {

	private KXTApplication application;
	private RequestQueue queue;

	private final static String LOGIN_URL = Constant.newBaseUrl + "/Appapi/Member/login";
	private String name, pwd;

	private EditText edit_name, edit_pwd, edit_check;
	private ImageView checkCodeImg;
	private Button login, register;
	private ImageView back;

	private SharedPreferences userinfo;
	private String from;
	private boolean isFromLive;
	private BounceTopEnter bas_in;
	private SlideBottomExit bas_out;
	private NormalDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (from != null && "live".equals(from)) {
			setTheme(R.style.myDialogTheme);
		}
		setContentView(R.layout.activity_login_two);
		userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);

		// 获取RequestQueue实例
		application = (KXTApplication) getApplication();
		queue = application.getQueue();
		if (queue == null) {
			queue = Volley.newRequestQueue(this);
		}

		from = getIntent().getStringExtra("from");
		if (from != null && "live".equals(from)) {
			isFromLive = true;
			setDialogStyle(1);
		} else {
			isFromLive = false;
		}
		findview();
	}

	private void findview() {
		// TODO Auto-generated method stub
		if (isFromLive) {
			findViewById(R.id.title).setVisibility(View.GONE);
			findViewById(R.id.login_logo).setVisibility(View.GONE);
			findViewById(R.id.loginone_register).setVisibility(View.GONE);
		} else {
			findViewById(R.id.title).setVisibility(View.VISIBLE);
		}

		edit_name = (EditText) findViewById(R.id.login_name);
		edit_pwd = (EditText) findViewById(R.id.login_pwd);
		edit_check = (EditText) findViewById(R.id.login_cheak);

		checkCodeImg = (ImageView) findViewById(R.id.login_code);
		checkCodeImg.setImageBitmap(Code.getInstance().createBitmap());
		checkCodeImg.setOnClickListener(this);

		back = (ImageView) findViewById(R.id.self_fk_img);
		back.setOnClickListener(this);

		login = (Button) findViewById(R.id.loginone_login);
		register = (Button) findViewById(R.id.loginone_register);

		login.setOnClickListener(this);
		register.setOnClickListener(this);
	}

	/**
	 * 登录
	 */
	private void Login() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("account", name);
		map.put("password", pwd);

		NormalPostRequest normalPostRequest = new NormalPostRequest(LOGIN_URL, new Listener<JSONObject>() {

			private SCDataSqlte dataSqlte;

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				String code;
				try {
					code = arg0.getString("code");
					if ("200".equals(code)) {
						// 登录成功
						JSONObject data = arg0.getJSONObject("data");
						// "token": "457cede2bc6aa7a3683af6ffd4cb5a19",
						// "member_id": "1",
						// "expired_time": 1462350254,
						// "user_info": {
						// "id": "1",
						// "name": "青之羽",
						// "rid": "17"

						Editor editor = userinfo.edit();

						editor.putString("token", data.getString("token"));
						editor.putString("member_id", data.getString("member_id"));
						editor.putLong("expired_time", data.getLong("expired_time"));

						JSONObject user_info = data.getJSONObject("user_info");
						editor.putString("login_uid", user_info.getString("id"));
						editor.putString("login_name", user_info.getString("name"));
						editor.putString("login_rid", user_info.getString("rid"));

						editor.putBoolean("isFirstWTG",true);

						dataSqlte = new SCDataSqlte(Login_One.this);
						SQLiteDatabase db = dataSqlte.getReadableDatabase();
						Cursor cursor = db.rawQuery("select * from roomrole where id=?", new String[] { user_info.getString("rid") });
						while (cursor.moveToNext()) {
							editor.putString("r_name", cursor.getString(cursor.getColumnIndex("name")));
							editor.putString("limit_chat_time", cursor.getString(cursor.getColumnIndex("limit_chat_time")));
							editor.putString("limit_colorbar_time", cursor.getString(cursor.getColumnIndex("limit_colorbar_time")));
							editor.putString("power_visit_room", cursor.getString(cursor.getColumnIndex("power_visit_room")));
							editor.putString("image", cursor.getString(cursor.getColumnIndex("image")));
						}
						cursor.close();
						db.close();

						editor.commit();
						if (!isFromLive) {
							Intent intent = new Intent(Login_One.this, MainActivity.class);
							if ("self".equals(from))
								intent.putExtra("enter", "self");
							startActivity(intent);
						}
						finish();
					} else {
						// 登录失败,
						Toast.makeText(application, "登录失败," + arg0.getString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// 登录失败
					Toast.makeText(application, "登录失败," + e.toString(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// 登录失败
				Toast.makeText(application, "登录失败," + arg0, Toast.LENGTH_SHORT).show();
			}
		}, map);
		queue.add(normalPostRequest);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.loginone_login:
			if (checkCode()) {
				name = edit_name.getText().toString().trim();
				pwd = edit_pwd.getText().toString().trim();
				Login();
			} else {
				Toast.makeText(application, "验证失败", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.loginone_register:
			Intent intent = new Intent(this, Register_One.class);
			if (isFromLive)
				intent.putExtra("from", "live");
			startActivity(intent);
			break;
		case R.id.self_fk_img:
			onBackPressed();
			break;
		case R.id.login_code:
			// 刷新验证码
			checkCodeImg.setImageBitmap(Code.getInstance().createBitmap());
			break;
		}
	}

	/**
	 * 验证码检验
	 * 
	 * @return
	 */
	private boolean checkCode() {
		// TODO Auto-generated method stub
		try {
			String input = edit_check.getText().toString().trim().toUpperCase();
			String code = Code.getInstance().getCode().toUpperCase();
			if (input.equals(code))
				return true;
			else
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}

	}

	private void setDialogStyle(int i) {
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		float size = DisplayUtilJYH.getDpi(this);
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度
		if (i == 1) {
			p.height = (int) ((d.getHeight() - (float) 40 * size - DisplayUtilJYH.getStatuBarHeight(this)) / 2.75 * 1.75);
			getWindow().setAttributes(p); // 设置生效
			getWindow().setGravity(Gravity.BOTTOM); // 设置在底部
		} else {
			p.height = (int) (d.getHeight() * 1.0); // 高度设置为屏幕的1.0
			getWindow().setAttributes(p); // 设置生效
			getWindow().setGravity(Gravity.CENTER); // 设置在底部
		}
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
		// TODO Auto-generated method stub
		if (GotyeLiveActivity.live != null) {
			((ischangeTitle) (GotyeLiveActivity.live)).changeTitle();
		}

		if ("1".equals(userinfo.getString("require_login", "0"))) {

			bas_in = new BounceTopEnter();
			bas_out = new SlideBottomExit();
			dialog = new NormalDialog(this);
			dialog.isTitleShow(false)
			// 设置背景颜色
					.bgColor(Color.parseColor("#383838"))
					// 设置dialog角度
					.cornerRadius(5)
					// 设置内容
					.content("是否确定退出程序?")
					// 设置居中
					.contentGravity(Gravity.CENTER)
					// 设置内容字体颜色
					.contentTextColor(Color.parseColor("#ffffff"))
					// 设置线的颜色
					.dividerColor(Color.parseColor("#222222"))
					// 设置字体
					.btnTextSize(15.5f, 15.5f)
					// 设置取消确定颜色
					.btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
					.btnPressColor(Color.parseColor("#2B2B2B"))//
					.widthScale(0.85f)//
					.showAnim(bas_in)//
					.dismissAnim(bas_out)//
					.show();

			dialog.setOnBtnClickL(new OnBtnClickL() {
				@Override
				public void onBtnClick() {
					dialog.dismiss();
				}
			}, new OnBtnClickL() {
				@Override
				public void onBtnClick() {
					dialog.dismiss();
					if (MainActivity.main != null && !MainActivity.main.isDestroyed())
						MainActivity.main.finish();
					finish();
					application.exitAppAll();
					System.exit(0);
					System.gc();
				}
			});

		} else {
			if ("self".equals(from)) {
				if (MainActivity.main != null && !MainActivity.main.isDestroyed()) {
					super.finish();
				} else {
					Intent intent = new Intent(Login_One.this, MainActivity.class);
					intent.putExtra("enter", "self");
					intent.putExtra("isLoadImg", false);
					startActivity(intent);
				}
			} else {
				super.finish();
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
