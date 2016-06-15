package com.jyh.hjtzdxt.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.gotye.live.core.Code;
import com.gotye.live.core.GLCore;
import com.gotye.live.core.model.AuthToken;
import com.gotye.live.core.model.RoomIdType;
import com.jyh.hjtzdxt.GotyeLiveActivity;
import com.jyh.hjtzdxt.R;
import com.jyh.hjtzdxt.WebActivity2;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;

/**
 * @author Administrator
 *
 */
public class fragment_zb extends Fragment implements OnClickListener {

	private ImageView imgJianjie, imgKufu;
	private boolean isCanJoin;
	private Intent intent;
	private SharedPreferences appinfo, userinfo;
	private SCDataSqlte dbhelp;
	private SQLiteDatabase db;
	private Intent intent2;

	// Gotye视频所需参数
	private boolean isCancel = false;
	private LoginThread mLoginThread;
	private ProgressDialog loginDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_zb, null);
		appinfo = getActivity().getSharedPreferences("appinfo", Context.MODE_PRIVATE);
		userinfo = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		dbhelp = new SCDataSqlte(getActivity());
		db = dbhelp.getReadableDatabase();
		isCanJoin = isCanJoin(db);
		findView(view);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				joinLive();
			}
		});
		return view;
	}

	private void findView(View view) {
		// TODO Auto-generated method stub
		// imgEnter = (ImageView) view.findViewById(R.id.img_enter);
		imgJianjie = (ImageView) view.findViewById(R.id.img_jianjie);
		imgKufu = (ImageView) view.findViewById(R.id.img_kefu);

		// imgEnter.setOnClickListener(this);
		imgJianjie.setOnClickListener(this);
		imgKufu.setOnClickListener(this);
		intent2 = new Intent(getActivity(), WebActivity2.class);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.img_enter:
		// joinLive();
		// break;
		case R.id.img_jianjie:
			// startActivity(new Intent(getActivity()(),JJActivity.class));
			intent2.putExtra(
					"url",
					getActivity().getSharedPreferences("appinfo", Context.MODE_PRIVATE).getString("summary_url",
							"http://appapi.feinong77.com/room/summary"));
			intent2.putExtra("from", "main");
			intent2.putExtra("title", "直播室简介");
			startActivity(intent2);
			break;
		case R.id.img_kefu:
			intent2.putExtra(
					"url",
					getActivity().getSharedPreferences("appinfo", Context.MODE_PRIVATE).getString("kefu_url",
							"http://newv9.local/Appapi/Index/kefu.html"));
			intent2.putExtra("from", "main");
			intent2.putExtra("title", "联系客服");
			startActivity(intent2);
			break;

		default:
			break;
		}
	}

	/**
	 * 进入直播间
	 */
	private void joinLive() {
		if (isCanJoin) {
			String type = appinfo.getString("video-type1", null);
			Log.i("type1", type);
			if (type != null) {
				if ("gensee".equals(type)) {
					Toast.makeText(getActivity(), "数据加载错误", 0).show();
				} else
					// intent = new Intent(getActivity()(),
					// GotyeLiveActivity.class);
					attemptLogin();
			}
		} else {
			Toast.makeText(getActivity(), "权限不够,请登录", 0).show();
		}
	}

	private void attemptLogin() {

		KXTApplication.player.stop();
		KXTApplication.core.clearAuth();
		KXTApplication.IsOut=false;

		KXTApplication.isFirst = true;
		mLoginThread = null;
		// Store values at the time of the login attempt.
		// String roomId = "100030";
		// String password = "000000";
		// String roomId = "101639";
		// String password = "333333";
		String roomId = appinfo.getString("video-Gotyeroomid", "");
		String password = appinfo.getString("video-Gotyepassword", "000000");
		String nickname = "111";

		boolean cancel = false;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			cancel = true;
		}

		if (TextUtils.isEmpty(roomId)) {
			cancel = true;
		}

		if (TextUtils.isEmpty(nickname)) {
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
		} else {
			save(roomId, password, nickname);
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			loginDialog = new ProgressDialog(getActivity());
			loginDialog.setMessage("进入直播室。。。");
			loginDialog.setCancelable(true);
			loginDialog.setCanceledOnTouchOutside(true);
			loginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (mLoginThread != null) {
						mLoginThread.cancel();
						mLoginThread = null;

						handleProgress(false);
						KXTApplication.core.clearAuth();
						return;
					}
				}
			});
			handleProgress(true);

			mLoginThread = new LoginThread(roomId, password, nickname, RoomIdType.GOTYE);
			mLoginThread.start();
		}
	}

	/**
	 * 获取用户进入直播间的权限
	 * 
	 * @param db
	 * @return
	 */
	private boolean isCanJoin(SQLiteDatabase db) {

		String can = userinfo.getString("power_visit_room", "0");
		if ("1".equals(can))
			return true;
		else
			return false;
	}

	/**
	 * 获取用户角色ID
	 * 
	 * @return
	 */
	private String getroleId() {
		String token = userinfo.getString("token", null);
		Long time = userinfo.getLong("expired_time", 0);
		if (token != null && time > System.currentTimeMillis() / 1000) {
			return userinfo.getString("role_id", "-1");
		} else {
			return appinfo.getString("role_id", "-1");
		}
	}

	private class LoginThread extends Thread {

		String roomId, password, nickaname;
		RoomIdType type;

		public LoginThread(String roomId, String password, String nickname, RoomIdType type) {
			this.roomId = roomId;
			this.password = password;
			this.nickaname = nickname;
			this.type = type;
			isCancel = false;
		}

		@Override
		public void run() {
			super.run();

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// 如果在登录时取消，则退出session
					if (isCancel) {
						KXTApplication.core.clearAuth();
						mLoginThread = null;
						handleProgress(false);
						return;
					}
					// 首先到服务器验证session取得accessToken和role等信息
					KXTApplication.core.auth(roomId, password, null, nickaname, type, new GLCore.Callback<AuthToken>() {
						@Override
						public void onCallback(int i, final AuthToken authToken) {
							if (isCancel || i != Code.SUCCESS) {
								// session验证失败
								KXTApplication.core.clearAuth();
								mLoginThread = null;
								handleProgress(false);
								Toast.makeText(getActivity(), "session验证失败", Toast.LENGTH_LONG).show();
								return;
							}
							handleProgress(false);
							Intent intent = new Intent(getActivity(), GotyeLiveActivity.class);
							startActivity(intent);
						}
					});

				}
			});
		}

		public void cancel() {
			isCancel = true;
			KXTApplication.core.clearAuth();
		}
	}

	private boolean isPasswordValid(String password) {
		// TODO: Replace this with your own logic
		return password.length() > 1;
	}

	private void save(String roomId, String password, String nickname) {
		SharedPreferences sp = getActivity().getSharedPreferences("config", 0);
		SharedPreferences.Editor ed = sp.edit();
		ed.putString("roomId", roomId);
		ed.putString("password", password);
		ed.putString("nickname", nickname);
		ed.commit();
	}

	private void handleProgress(final boolean show) {
		if (show) {
			loginDialog.show();
		} else {
			loginDialog.dismiss();
		}
	}
}
