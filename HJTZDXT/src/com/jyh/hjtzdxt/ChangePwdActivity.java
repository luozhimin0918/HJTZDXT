package com.jyh.hjtzdxt;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.tool.NormalPostRequest;

public class ChangePwdActivity extends Activity implements OnClickListener {

	private EditText old_pwd, new_pwd, re_pwd;
	private Button btn;
	private String token;
	private SharedPreferences preferences;
	private LinearLayout back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
			this.setTheme(R.style.BrowserThemeDefault);
		setContentView(R.layout.activity_changepwd);
		bindView();

	}

	private void bindView() {
		// TODO Auto-generated method stub
		old_pwd = (EditText) findViewById(R.id.changepwd_old);
		new_pwd = (EditText) findViewById(R.id.changepwd_new);
		re_pwd = (EditText) findViewById(R.id.changepwd_re);
		back = (LinearLayout) findViewById(R.id.changepwd_back);
		btn = (Button) findViewById(R.id.changepwd_btn);

		btn.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.changepwd_btn:
			if (!checkEdit())
				return;
			ChangePwd();
			break;
		case R.id.changepwd_back:
			finish();
		default:
			break;
		}
	}

	/**
	 * 提交新密码
	 */
	protected void ChangePwd() {
		// TODO Auto-generated method stub

		String changpwdUrl = Constant.newBaseUrl + "/Appapi/Member/editPwd";

		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("oldpass", old_pwd.getText().toString().trim());
		map.put("newpass", new_pwd.getText().toString().trim());
		map.put("repeatpass", re_pwd.getText().toString().trim());
		
		NormalPostRequest normalPostRequest=new NormalPostRequest(changpwdUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				try {
					if ("200".equals(arg0.getString("code"))) {
						Toast.makeText(ChangePwdActivity.this, "密码更改成功", Toast.LENGTH_LONG).show();
					} else
						Toast.makeText(ChangePwdActivity.this, arg0.getString("msg"), Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(ChangePwdActivity.this, "密码不合法"+arg0, Toast.LENGTH_LONG).show();
			}
		}, map);
		
		Volley.newRequestQueue(this).add(normalPostRequest);
//		JsonObjectRequest request = new JsonObjectRequest(changpwdUrl, null, new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject arg0) {
//				// TODO Auto-generated method stub
//				Log.i("info", arg0.toString());
//				try {
//					if ("200".equals(arg0.getString("code"))) {
//						Toast.makeText(ChangePwdActivity.this, "密码更改成功", Toast.LENGTH_LONG).show();
//					} else
//						Toast.makeText(ChangePwdActivity.this, arg0.getString("msg"), Toast.LENGTH_LONG).show();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				// TODO Auto-generated method stub
//				Toast.makeText(ChangePwdActivity.this, "密码不合法", Toast.LENGTH_LONG).show();
//			}
//		});
//
//		Volley.newRequestQueue(this).add(request);
	}

	/**
	 * 检查输入是否合法
	 * 
	 * @return
	 */
	private boolean checkEdit() {
		token = getSharedPreferences("userinfo", Context.MODE_PRIVATE).getString("token", null);
		String new_pwdStr = new_pwd.getText().toString().trim();
		int pwd_length = new_pwdStr.length();
		if (old_pwd.getText().toString().trim().equals("")) {
			Toast.makeText(ChangePwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
		} else if (new_pwdStr.equals("")) {
			Toast.makeText(ChangePwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
		} else if (pwd_length < 6 || pwd_length > 32) {
			Toast.makeText(ChangePwdActivity.this, "密码最少6个字符，最多32个字符", Toast.LENGTH_SHORT).show();
		} else if (new_pwdStr.equals(old_pwd.getText().toString().trim())) {
			Toast.makeText(ChangePwdActivity.this, "新密码不能与旧密码一样", Toast.LENGTH_SHORT).show();
		} else if (!new_pwdStr.equals(re_pwd.getText().toString().trim())) {
			Toast.makeText(ChangePwdActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
		} else if (token == null) {
			Toast.makeText(ChangePwdActivity.this, "登录已失效，请重新登录", Toast.LENGTH_SHORT).show();
		} else {
			return true;
		}
		return false;
	}
}
