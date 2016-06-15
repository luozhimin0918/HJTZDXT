package com.jyh.hjtzdxt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.tool.MsgDialog;
import com.jyh.hjtzdxt.tool.NormalPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeNameActivity extends Activity implements OnClickListener {
	private SharedPreferences preferences;

	private EditText name;
	private Button btn;
	private LinearLayout back;
	private String token;
	private String nameStr;
	private SharedPreferences userinfo;
	private RequestQueue queue;

	private String newName;// 新昵称
	private String allow_edit;// EditText是否可编辑
	private String statu;// 新昵称审核状态

	protected String Msg;
	private boolean isFirstWTG;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 初始化EditText
				if (newName != null)
					name.setText(newName);
				else
					name.setText(nameStr);
				break;
			case 2:
				// 不可编辑
				name.setFocusable(false);
				name.setFocusableInTouchMode(false);
				btn.setClickable(false);
				break;
			case 3:
				name.setFocusableInTouchMode(true);
				name.setFocusable(true);
				name.requestFocus();
				btn.setClickable(true);
				break;
			case 4:
				// 审核未通过
				name.setText(nameStr);
				break;
			case 5:
				// 审核通过
				name.setText(nameStr);
				Editor editor = userinfo.edit();
				editor.putString("login_name", nameStr);
				editor.commit();
				setResult(666);
			case 6:
				// 直接审核通过
				Editor editor2 = userinfo.edit();
				editor2.putString("login_name", name.getText().toString().trim());
				editor2.commit();
				setResult(666);
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
		this.setTheme(R.style.BrowserThemeDefault);

		setContentView(R.layout.activity_changename);
		userinfo = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

		token = userinfo.getString("token", null);
		nameStr = userinfo.getString("login_name", null);
		queue = Volley.newRequestQueue(this);

		findView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("info", "changeOnresume");
		isFirstWTG=userinfo.getBoolean("isFirstWTG",true);
		getChangeNameState(token);
	}

	private void findView() {
		name = (EditText) findViewById(R.id.changename_name);
		btn = (Button) findViewById(R.id.changename_btn);
		back = (LinearLayout) findViewById(R.id.changename_back);

		if (nameStr != null)
			name.setHint(nameStr);

		btn.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.changename_btn:
			if (!checkEdit())
				return;
			changeName();
			break;
		case R.id.changename_back:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 更改昵称
	 */
	private void changeName() {
		String changeNameUrl = Constant.newBaseUrl + "/Appapi/Member/editName";
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("name", name.getText().toString().trim());
		NormalPostRequest request = new NormalPostRequest(changeNameUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				Log.i("info", "changNameResult=" + arg0.toString());
				try {
					String code = arg0.getString("code");
					if ("200".equals(code)) {
						new MsgDialog(ChangeNameActivity.this, arg0.getString("msg") != null ? arg0.getString("msg") : "").show();
						Editor editor=userinfo.edit();
						editor.putBoolean("isFirstWTG", true);
						editor.commit();
					} else if ("300".equals(code)) {
						new MsgDialog(ChangeNameActivity.this, arg0.getString("msg") != null ? arg0.getString("msg") : "").show();
						handler.sendEmptyMessage(3);
					} else if ("401".equals(code)) {
						new MsgDialog(ChangeNameActivity.this, "Token已过期，请重新登录").show();
						// TODO Auto-generated catch block
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {

			}
		}, map);
		queue.add(request);
	}

	/*
	 * 获取更改昵称状态
	 */
	private void getChangeNameState(String token) {
		String changeNameStateUrl = Constant.newBaseUrl + "/Appapi/Member/nikeNameStatus";

		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);

		NormalPostRequest normalPostRequest = new NormalPostRequest(changeNameStateUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				try {
					String code = arg0.getString("code");
					if ("200".equals(code)) {
						Log.i("hehe", arg0.toString());
						JSONObject data = arg0.getJSONObject("data");
						String isOver = data.getString("check_nickname");
						String name = data.getString("nickname");
						String newname = data.getString("new_nickname");
						switch (isOver) {
						case "-1":
							// 未通过
							nameStr = name;
							if (isFirstWTG) {
								new MsgDialog(ChangeNameActivity.this, "您的昵称未通过审核").show();
								Editor editor=userinfo.edit();
								editor.putBoolean("isFirstWTG", false);
								editor.commit();
							}
							handler.sendEmptyMessage(3);
							handler.sendEmptyMessage(4);
							break;
						case "0":
							// 审核中
							nameStr = newname;
							if (isFirstWTG) {
								new MsgDialog(ChangeNameActivity.this, "您的昵称正在审核中").show();
								Editor editor=userinfo.edit();
								editor.putBoolean("isFirstWTG", false);
								editor.commit();
							}
							handler.sendEmptyMessage(2);
							handler.sendEmptyMessage(4);
							break;
						case "1":
							// 已通过
							nameStr = name;
							if (isFirstWTG) {
								new MsgDialog(ChangeNameActivity.this, "您的昵称已通过审核").show();
								Editor editor=userinfo.edit();
								editor.putBoolean("isFirstWTG", false);
								editor.commit();
							}
							handler.sendEmptyMessage(3);
							handler.sendEmptyMessage(5);

							break;
						}

					} else {
						new MsgDialog(ChangeNameActivity.this, "获取更改昵称状态信息失败：" + arg0.getString("msg")).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					try {
						new MsgDialog(ChangeNameActivity.this, "获取更改昵称状态信息失败：" + arg0.getString("msg")).show();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				new MsgDialog(ChangeNameActivity.this, "获取更改昵称状态信息失败：" + arg0).show();
			}
		}, map);

		queue.add(normalPostRequest);
	}

	/**
	 * 检测输入是否合法
	 * 
	 * @return
	 */
	private boolean checkEdit() {

		if (name.getText().toString().trim().equals("")) {
			Toast.makeText(ChangeNameActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
		} else if (token == null) {
			Toast.makeText(ChangeNameActivity.this, "游客没有更改昵称的权限", Toast.LENGTH_SHORT).show();
		} else if (nameStr.equals(name.getText().toString().trim())) {
			Toast.makeText(ChangeNameActivity.this, "新昵称与旧昵称一致，请重新输入", Toast.LENGTH_SHORT).show();
		} else {
			return true;
		}
		return false;
	}

}
