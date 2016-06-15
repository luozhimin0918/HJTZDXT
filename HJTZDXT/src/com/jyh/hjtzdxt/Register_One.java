package com.jyh.hjtzdxt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;
import com.jyh.hjtzdxt.tool.DisplayUtilJYH;
import com.jyh.hjtzdxt.tool.NormalPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register_One extends Activity implements OnClickListener {

	private final static String REGISTER_URL = Constant.newBaseUrl + "/Appapi/Member/register";

	private EditText edit_account, edit_name, edit_pwd, edit_repwd;
	private WebView webView;
	private Button register;
	private ImageView back;
	private String account, name, pwd, repwd;

	private KXTApplication application;
	private RequestQueue queue;
	private SharedPreferences userinfo, appinfo;

	protected SCDataSqlte dataSqlte;

	private String from;

	protected boolean isFromLive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (from != null && "live".equals(from)) {
			setTheme(R.style.myDialogTheme);
		}
		setContentView(R.layout.activity_registrt_one);

		userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
		appinfo = getSharedPreferences("appinfo", MODE_PRIVATE);
		application = (KXTApplication) getApplication();
		queue = application.getQueue();
		if (queue == null)
			queue = Volley.newRequestQueue(application);

		from = getIntent().getStringExtra("from");

		if (from != null && "live".equals(from)) {
			isFromLive = true;
			setDialogStyle(1);
		} else {
			isFromLive = false;
		}

		findview();
		final String url = appinfo.getString("register_url", null);
		if (url != null && !"".equals(url)) {
			webView.setVisibility(View.VISIBLE);
			findViewById(R.id.local).setVisibility(View.GONE);

			WebSettings webSeting = webView.getSettings();
			webSeting.setJavaScriptEnabled(true);
			webSeting.setLoadsImagesAutomatically(true);
			webSeting.setLoadWithOverviewMode(true);
			webSeting.setUseWideViewPort(true);
			webSeting.setBuiltInZoomControls(true);
			webView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}

				@Override
				public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
					if (url.startsWith("http") || url.startsWith("https")) {
						return super.shouldInterceptRequest(view, url);
					} else {
						Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(in);
						return null;
					}
				}

				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					// TODO Auto-generated method stub
					super.onReceivedError(view, errorCode, description, failingUrl);
					view.loadUrl(url);
				}

			});
			webView.loadUrl(url);

		} else {
			webView.setVisibility(View.GONE);
			findViewById(R.id.local).setVisibility(View.VISIBLE);
		}

	}

	private void findview() {
		// TODO Auto-generated method stub
		webView = (WebView) findViewById(R.id.web);
		edit_account = (EditText) findViewById(R.id.registerone_account);
		edit_name = (EditText) findViewById(R.id.registerone_name);
		edit_pwd = (EditText) findViewById(R.id.registerone_pwd);
		edit_repwd = (EditText) findViewById(R.id.registerone_repwd);
		back = (ImageView) findViewById(R.id.registerone_back);

		if (from != null && "live".equals(from)) {
			findViewById(R.id.title).setVisibility(View.GONE);
			back.setVisibility(View.GONE);
		} else {
			findViewById(R.id.title).setVisibility(View.VISIBLE);
			back.setVisibility(View.VISIBLE);
		}
		register = (Button) findViewById(R.id.registerone_register);
		register.setOnClickListener(this);

		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.registerone_register:
			account = edit_account.getText().toString().trim();
			name = edit_name.getText().toString().trim();
			pwd = edit_pwd.getText().toString().trim();
			repwd = edit_repwd.getText().toString().trim();
			register();
			break;
		case R.id.registerone_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	private void register() {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		map.put("account", account);
		map.put("nickname", name);
		map.put("password", pwd);
		map.put("repassword", repwd);
		NormalPostRequest normalPostRequest = new NormalPostRequest(REGISTER_URL, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				String code;
				try {
					code = arg0.getString("code");
					if ("200".equals(code)) {
						// 登录成功
						JSONObject data = arg0.getJSONObject("data");
						Log.i("hehe", arg0.toString());

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
						dataSqlte = new SCDataSqlte(Register_One.this);
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
							Intent intent = new Intent(Register_One.this, MainActivity.class);
							if ("self".equals(from))
								intent.putExtra("enter", "self");
							startActivity(intent);
						}
						finish();
					} else {
						// 注册失败,
						Toast.makeText(application, "注册失败," + arg0.getString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// 注册失败
					Toast.makeText(application, "注册失败," + e.toString(), Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(application, "注册失败," + arg0, Toast.LENGTH_SHORT).show();
			}
		}, map);

		queue.add(normalPostRequest);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (from == null || !"live".equals(from)) {
			startActivity(new Intent(application, Login_One.class));
		}
		finish();
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
		// ...but notify us that it happened.   
		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
		// Note that flag changes must happen *before* the content view is set.
		getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
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
		super.finish();
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
