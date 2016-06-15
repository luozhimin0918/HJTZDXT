package com.jyh.hjtzdxt.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.hjtzdxt.GotyeLiveActivity;
import com.jyh.hjtzdxt.R;
import com.jyh.hjtzdxt.adapter.ChatMsgAdapter;
import com.jyh.hjtzdxt.bean.ChatMsgEntity;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.customtool.FaceRelativeLayout;
import com.jyh.hjtzdxt.customtool.MyListView;
import com.jyh.hjtzdxt.tool.ImageDownLoader;
import com.jyh.hjtzdxt.tool.NormalPostRequest;

@SuppressWarnings("deprecation")
public class fragment_chat extends Fragment implements OnClickListener {
	private View view;
	private ImageView mBtnSend;
	private EditText mEditTextContent;
	String username, massg;
	private MyListView mListView;
	private ChatMsgAdapter mAdapter;
	private InputMethodManager imm;
	private ReceiveBroadCast receiveBroadCast;// 广播接受者(可用回调实现，暂时用广播代替)
	private List<ChatMsgEntity> mDataArrays;// 及时数据
	private boolean iscon = false;
	private KXTApplication application;
	private ImageDownLoader loader;
	private Timer timer;
	private TextView mtv_send;
	private int i;
	private SharedPreferences preferences, userinfo;
	private String from_name, from_roid, uid, toKen;
	private Bundle bundle;

	private int historysize;// 历史数据数量

	private NewChatRecevicer chatRecevicer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		application = (KXTApplication) getActivity().getApplication();

		preferences = getActivity().getSharedPreferences("appinfo", Context.MODE_PRIVATE);
		userinfo = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		i = Integer.parseInt(userinfo.getString("limit_chat_time", "15")) + 1;

		from_name = userinfo.getString("name", "游客-n291h");
		from_roid = preferences.getString("rid", "1");
		uid = userinfo.getString("uid", "-46182724291");

		queue = application.getQueue();
		if (queue == null)
			queue = Volley.newRequestQueue(getActivity());

		if (application.getChatMsgEntities() == null) {
			GetHostoryData();
		} else {
			historysize = application.getChatMsgEntities().size();
		}

		chatRecevicer = new NewChatRecevicer();
		getActivity().registerReceiver(chatRecevicer, new IntentFilter("newChatData"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("chatActivity", "create");
		view = inflater.inflate(R.layout.layout_chat, null);

		// Log.i("result", "apkinfo    " + apkinfo);
		initView();
		if (bundle != null) {
			mDataArrays = getSavedInstanceState(bundle);// 得到Fragment销毁时保存的数据
		}
		Log.i("chatActivity", "saveInstance-get" + " bundle!=null " + (bundle != null) + " " + mDataArrays.toString());
		// initSocketIO();
		if (mAdapter == null) {

			mAdapter = new ChatMsgAdapter(getActivity(), mDataArrays, mListView);
			mListView.setAdapter(mAdapter);
		}
		return view;
	}

	private void getToken() {
		toKen = userinfo.getString("token", "");
		expired_time = userinfo.getLong("expired_time", 0);
		if (toKen != null && expired_time > (System.currentTimeMillis() / 1000)) {
			from_name = userinfo.getString("login_name", "游客-n291h");
			from_roid = userinfo.getString("login_rid", "1");
			uid = userinfo.getString("login_uid", "-46182724291");
		} else {
			uid = userinfo.getString("uid", "-46182724291");
			from_name = userinfo.getString("name", "游客-n291h");
			from_roid = userinfo.getString("rid", "1");
		}
	}

	private List<ChatMsgEntity> getSavedInstanceState(Bundle savedInstanceState) {
		List<ChatMsgEntity> chatMsgEntities = null;
		if (savedInstanceState != null) {
			// Log.i("keep", ".............");
			// ArrayList<String> chatMsgsStr =
			// savedInstanceState.getStringArrayList("chatMsg");
			// int i, len = chatMsgsStr.size();
			// ChatMsgEntity entity;
			// chatMsgEntities = new ArrayList<ChatMsgEntity>();
			// for (i = 0; i < len; i++) {
			// String[] str = chatMsgsStr.get(i).split(",");
			// try {
			// entity = new ChatMsgEntity();
			// // f_uid ,f_name,f_rid ,t_uid ,t_name,t_rid,data,
			// is_checked,time,id
			// entity.setF_uid(str[0]);
			// entity.setF_name(str[1]);
			// entity.setF_rid(str[2]);
			// entity.setT_uid(str[3]);
			// entity.setT_name(str[4]);
			// entity.setT_rid(str[5]);
			// entity.setData(str[6]);
			// entity.setIs_checked(str[7]);
			// entity.setTime(str[8]);
			// entity.setId(str[9]);
			//
			// chatMsgEntities.add(entity);
			// } catch (Exception e) {
			// e.printStackTrace();
			// chatMsgEntities.add(null);
			// }
			// }
			List<ChatMsgEntity> chatMsgsStr = savedInstanceState.getParcelableArrayList("chatMsg");
			int i, len = chatMsgsStr.size();
			chatMsgEntities = new ArrayList<ChatMsgEntity>();
			for (i = 0; i < len; i++) {
				try {
					ChatMsgEntity str = (ChatMsgEntity) chatMsgsStr.get(i);
					chatMsgEntities.add(str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			((KXTApplication) (getActivity().getApplication())).setChatMsgEntities(chatMsgEntities);
			historysize = chatMsgEntities.size();
		}
		return chatMsgEntities;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.obj instanceof ChatMsgEntity) {
				mDataArrays.add(mDataArrays.size(), (ChatMsgEntity) msg.obj);
				if (mDataArrays.size() > 50) {
					mDataArrays.remove(0);
				}
				mAdapter.setColl(mDataArrays);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mDataArrays.size() - 1);
			}
			switch (msg.what) {
			case 1:
				loader = new ImageDownLoader(getActivity());
				new Thread(new Runnable() {

					@Override
					public void run() {
						// loader.
					}
				}).start();
				break;
			case 2:
				if (i == 0 || i < 0) {
					timer.cancel();
					timer.purge();
					mBtnSend.setVisibility(View.VISIBLE);
					mtv_send.setVisibility(View.GONE);
					i = Integer.parseInt(userinfo.getString("limit_chat_time", "15")) + 1;
				}
				i = i - 1;
				mtv_send.setText("" + i);
				break;
			case 3:
				Toast.makeText(getActivity(), "输入字数超过范围", Toast.LENGTH_SHORT).show();
				break;
			case 11:
				Toast.makeText(getActivity(), "彩条功能还剩" + userinfo.getString("limit_colorbar_time", "15") + "秒", Toast.LENGTH_SHORT).show();
				break;
			case 22:
				Toast.makeText(getActivity(), "彩条功能可用", Toast.LENGTH_SHORT).show();
				break;
			case 23:
				mAdapter.notifyDataSetChanged();
				break;
			case 100:
				if (mDataArrays.size() > 50) {
					mDataArrays.remove(0);
				}
				mAdapter.setColl(mDataArrays);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mDataArrays.size() - 1);
				break;
			case 101:
				mAdapter = null;
				if (mAdapter == null) {
					mAdapter = new ChatMsgAdapter(getActivity(), mDataArrays, mListView);
					mListView.setAdapter(mAdapter);
				}
				break;
			case 120:
				mAdapter.setColl(mDataArrays);
				mAdapter.notifyDataSetChanged();
				break;
			case 111:
				if (activity instanceof GotyeLiveActivity) {
					((GotyeLiveActivity) activity).videoVisible();
				}
				break;
			case 112:
				if (activity instanceof GotyeLiveActivity) {
					((GotyeLiveActivity) activity).videoGone();
				}
				break;
			default:
				break;
			}
		};
	};
	private String contString;
	private Activity activity;
	private int i2 = 0;
	private int myChatPosition;
	private long expired_time;
	private RequestQueue queue;
	private String msgType;

	public void initView() {
		mDataArrays = new ArrayList<ChatMsgEntity>();

		mListView = (MyListView) view.findViewById(R.id.listview);
		mBtnSend = (ImageView) view.findViewById(R.id.btn_send);
		mtv_send = (TextView) view.findViewById(R.id.tv_send);
		mtv_send.setText("" + (i - 1));
		mBtnSend.setOnClickListener(this);
		mEditTextContent = (EditText) view.findViewById(R.id.et_sendmessage);
		mEditTextContent.setOnClickListener(this);
		if (receiveBroadCast == null) {
			receiveBroadCast = new ReceiveBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction("彩条发送"); // 只有持有相同的action的接受者才能接收此广播
			getActivity().registerReceiver(receiveBroadCast, filter);
		}
		if (application.getChatMsgEntities() != null)
			mDataArrays.addAll(application.getChatMsgEntities());
		else if (bundle != null)
			mDataArrays.addAll(getSavedInstanceState(bundle));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			FaceRelativeLayout.layout.close();
			if (activity instanceof GotyeLiveActivity) {
				((GotyeLiveActivity) activity).videoVisible();
			}
			if (mEditTextContent.getText().toString().length() < 120) {
				if (mEditTextContent.getText().toString().equals("") || mEditTextContent.getText().toString().length() <= 0) {
					Toast.makeText(getActivity(), "内容为空", Toast.LENGTH_SHORT).show();
				} else {
					send();
					timer = new Timer();
					setTimerTask();
					mBtnSend.setVisibility(View.INVISIBLE);
					mtv_send.setVisibility(View.VISIBLE);
				}
			} else {
				handler.sendEmptyMessage(3);
			}

			break;
		case R.id.et_sendmessage:
			// handler.sendEmptyMessage(112);
			if (activity instanceof GotyeLiveActivity) {
				((GotyeLiveActivity) activity).videoGone();
			}
			// handler.sendEmptyMessageAtTime(111, 2*1000);
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(111);
				}
			}, 2000);
			break;
		}
	}

	private void setTimerTask() {
		i = Integer.parseInt(userinfo.getString("limit_chat_time", "15")) + 1;
		mtv_send.setText("" + (i - 1));
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(2);
			}
		}, 1 * 1000, 1 * 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
	}

	private void send() {
		i2++;
		getToken();
		boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
		if (isOpen) {
			try {
				((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
			}
		}
		contString = mEditTextContent.getText().toString();
		msgType = null;
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			List<String> strings = application.getCaitiaos();
			boolean isCaitiao = false;
			for (String string : strings) {
				Log.i("info", "....." + string);
				if (contString.contains(string))
					isCaitiao = true;
			}
			if (isCaitiao) {
				entity.setData((":caitiao" + contString).replace(" ", ""));
				contString = (":caitiao" + contString).replace(" ", "");
				msgType = "caitiao";
			} else {
				entity.setData(contString);
				msgType = "public";
			}

			entity.setIs_checked("1");
			entity.setT_uid("");
			entity.setT_rid("");
			entity.setT_name("");
			entity.setF_name(from_name);
			entity.setF_rid(from_roid);

			entity.setTime("" + System.currentTimeMillis() / 1000);
			mDataArrays.add(entity);
			myChatPosition = historysize - 1 + i2;
			handler.sendEmptyMessage(100);
			mEditTextContent.setText("");
			// mListView.setSelection(mListView.getCount() - 1);
			SendMassage();

		}
	}

	private void SendMassage() {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		// cmd:message
		// token:37fdb20b00078ed4ab37cf1d472073cd //游客不用传token
		// f_uid:1
		// f_name:会员-102
		// f_rid:2
		// t_uid: 0
		// t_name: ""
		// t_rid: 0
		// data:sdfsdaf
		// time:1460106061
		// type:public
		map.put("cmd", "message");
		map.put("token", toKen);// 游客不用传token
		map.put("f_uid", uid);
		map.put("f_name", from_name);
		map.put("f_rid", from_roid);
		map.put("t_uid", "0");
		map.put("t_name", "");
		map.put("t_rid", "0");
		map.put("data", contString);
		map.put("time", "" + System.currentTimeMillis() / 1000);
		map.put("type", msgType);

		NormalPostRequest postRequest = new NormalPostRequest(Constant.newBaseUrl + "/Appapi/Chat/handle", new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				try {
					if ("200".equals(arg0.getString("code"))) {
						Toast.makeText(application, "消息发送成功", 0).show();
					} else if ("401".equals(arg0.getString("code"))) {
						Toast.makeText(application, "消息发送失败,Token已过期", 0).show();
					} else {
						Toast.makeText(application, "消息发送失败," + arg0.getString("msg"), 0).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(application, "消息发送失败," + arg0, 0).show();
			}
		}, map);
		queue.add(postRequest);
	}

	public class ReceiveBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 得到广播中得到的数据，并显示出来
			String message = intent.getStringExtra("send");
			if (message.equals("send")) {
				send();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getToken();
		if (receiveBroadCast == null) {
			receiveBroadCast = new ReceiveBroadCast();
			IntentFilter filter = new IntentFilter();
			filter.addAction("彩条发送"); // 只有持有相同的action的接受者才能接收此广播
			getActivity().registerReceiver(receiveBroadCast, filter);
		}
		if (iscon) {
			handler.sendEmptyMessage(101);
			i2 = 0;
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("chatActivity", "stop");
		iscon = true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("chatActivity", "destory");
		application.setChatMsgEntities(mDataArrays);
		getActivity().unregisterReceiver(receiveBroadCast);
		getActivity().unregisterReceiver(chatRecevicer);
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("chatActivity", "saveInstance");
		List<ChatMsgEntity> chatMsgs = mDataArrays;
		ArrayList<ChatMsgEntity> chatMsgsStr = new ArrayList<ChatMsgEntity>();
		int i, len = chatMsgs.size();
		for (i = 0; i < len; i++) {
			try {
				chatMsgsStr.add(chatMsgs.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ArrayList<String> chatMsgsStr = new ArrayList<String>();
		// for (i = 0; i < len; i++) {
		// try {
		// chatMsgsStr.add(chatMsgs.get(i).toString());
		// } catch (Exception e) {
		// chatMsgsStr.add(",,,,,,,,,");
		// }
		// }
		// outState.putStringArrayList("chatMsg", chatMsgsStr);
		outState.putParcelableArrayList("chatMsg", chatMsgsStr);
		((KXTApplication) getActivity().getApplication()).setChatMsgEntities(mDataArrays);
		bundle = outState;
		// Log.i("chatActivity", "saveInstance-over " + chatMsgsStr);
		i2 = 0;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}

	/**
	 * 获取历史信息
	 */
	private void GetHostoryData() {

		List<ChatMsgEntity> chatMsgEntities = new ArrayList<ChatMsgEntity>();

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(Constant.newBaseUrl + "/Appapi/Chat/history");
		try {
			HttpResponse response = client.execute(get);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity, "GBK");
				JSONArray array = new JSONObject(data).getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);

					ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
					if (object.getString("f_name") != null && !object.getString("f_name").equals("") && object.getString("t_name") != null) {

						chatMsgEntity.setData(object.getString("data"));
						chatMsgEntity.setIs_checked(object.getString("is_checked"));
						chatMsgEntity.setT_uid(object.getString("t_uid"));
						chatMsgEntity.setT_rid(object.getString("t_rid"));
						chatMsgEntity.setT_name(object.getString("t_name"));
						chatMsgEntity.setF_name(object.getString("f_name"));
						chatMsgEntity.setF_rid(object.getString("f_rid"));
						chatMsgEntity.setF_uid(object.getString("f_uid"));
						chatMsgEntity.setTime(object.getString("time"));
						chatMsgEntity.setId(object.getString("id"));

						chatMsgEntities.add(chatMsgEntity);
						historysize = chatMsgEntities.size();
						application.setChatMsgEntities(chatMsgEntities);
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 监听新信息
	 * 
	 * @author Administrator
	 *
	 */
	class NewChatRecevicer extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent != null) {
				String cmd = intent.getStringExtra("cmd");
				if ("message".equals(cmd)) {
					ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
					chatMsgEntity.setData(intent.getStringExtra("data"));
					chatMsgEntity.setIs_checked(intent.getStringExtra("is_checked"));
					chatMsgEntity.setT_uid(intent.getStringExtra("t_uid"));
					chatMsgEntity.setT_rid(intent.getStringExtra("t_rid"));
					chatMsgEntity.setT_name(intent.getStringExtra("t_name"));
					chatMsgEntity.setF_name(intent.getStringExtra("f_name"));
					chatMsgEntity.setF_rid(intent.getStringExtra("f_rid"));
					chatMsgEntity.setF_uid(intent.getStringExtra("f_uid"));
					chatMsgEntity.setTime(intent.getStringExtra("time"));
					chatMsgEntity.setId(intent.getStringExtra("id"));
					if (!uid.equals(intent.getStringExtra("f_uid"))) {
						mDataArrays.add(chatMsgEntity);
						Collections.sort(mDataArrays, new ComparatorChatMsg());
					}
				} else if ("delMsg".equals(cmd)) {
					String id = intent.getStringExtra("id");
					int size = mDataArrays.size();
					ChatMsgEntity entity = null;
					for (int i = size - 1; i >= 0; i--) {
						entity = mDataArrays.get(i);
						if (id.equals(entity.getId())) {
							mDataArrays.remove(entity);
							break;
						}
						// else if (null == entity.getId() ||
						// "".equals(entity.getId())) {
						// Toast.makeText(application, "您的信息：“" +
						// entity.getData() + "”不符合相关要求，已被删除", 0).show();
						// mDataArrays.remove(entity);
						// break;
						// }
					}
				}
				handler.sendEmptyMessage(100);
			}
		}
	}

	public class ComparatorChatMsg implements Comparator {

		public int compare(Object arg0, Object arg1) {
			ChatMsgEntity user0 = (ChatMsgEntity) arg0;
			ChatMsgEntity user1 = (ChatMsgEntity) arg1;

			if (user1 == null || user0 == null)
				return 0;
			if (user1.getTime() == null || user0.getTime() == null)
				return 0;
			int flag = user0.getTime().compareTo(user1.getTime());
			if (flag == 0) {
				if (user1.getId() == null || user0.getId() == null)
					return 0;
				return user0.getId().compareTo(user1.getId());
			} else {
				return flag;
			}
		}

	}
}
