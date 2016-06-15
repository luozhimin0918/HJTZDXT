package com.jyh.hjtzdxt;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gotye.live.player.Code;
import com.gotye.live.player.GLPlayer;
import com.gotye.live.player.PlayerListener;
import com.jyh.hjtzdxt.WebActivity.ischangeTitle;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.customtool.MyViewPager2;
import com.jyh.hjtzdxt.fragment.fragment_chat;
import com.jyh.hjtzdxt.fragment.fragment_function;
import com.jyh.hjtzdxt.fragment.fragment_function.changetitle;
import com.jyh.hjtzdxt.fragment.fragment_kefu;
import com.jyh.hjtzdxt.fragment.fragment_web;
import com.jyh.hjtzdxt.service.ChatService;
import com.jyh.hjtzdxt.socket.ServiceUtil;
import com.jyh.hjtzdxt.view.ChangeDirection;
import com.jyh.hjtzdxt.view.GLSurfaceViewContainer;

/**
 * @author beginner
 * @date 创建时间：2015年7月21日 下午4:53:38
 * @version 1.0
 */
public class GotyeLiveActivity extends FragmentActivity implements OnClickListener, PlayerListener, ChangeDirection, changetitle,
		ischangeTitle, _Activity.ischangeTitle, VideoGone {

	private ImageView chatBtn, serviceBtn, listBtn, functionBtn, backBtn, fullBtn;
	private LinearLayout chatbg, servicebg, listbg, functionbg, menu, line;
	private FrameLayout layout;
	private MyViewPager2 viewPager;
	private SharedPreferences preferences, appinfo;
	private InputMethodManager imm;
	private LinearLayout.LayoutParams params;
	private Display display;
	private boolean isShow;// 判断当前全屏按钮是否显示

	private boolean isDialogShow = true;
	private boolean isDialogTextShow = false;
	private String dialogText = "";

	// 视频下方的小界面
	private fragment_chat fragment_chat;
	private fragment_function fragment_function;
	private fragment_kefu fragment_kf;
	private fragment_web fragment_hq;
	private List<Fragment> fragments;
	private DemoAdapter adapter;

	private int screenWidth, screenHeight;// 屏幕宽高

	private Timer timer = new Timer();
	public static Activity live;
	private boolean isChange = false;// 用来判断功能界面是否刷新(登录/我的)

	private int num;
	private CallPhoneBroadcaseReceiver receiver;
	private boolean isFull;// 是否为全屏

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LiveActivity", "create");
		live = this;
		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
		setTheme(R.style.BrowserThemeDefault);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		// 禁止黑屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_gotyelive);
		screenOffOrOn();
		appinfo = getSharedPreferences("appinfo", Context.MODE_PRIVATE);
		findView();
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			GotyeLiveActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置横屏
			fullBtn.setSelected(false);
			isFull = true;
		} else
			isFull = false;
		setTimerTask();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PHONE_STATE");
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		receiver = new CallPhoneBroadcaseReceiver();
		registerReceiver(receiver, filter);

		bindService();
	}

	// Gotye
	private RelativeLayout surfaceViewContainer;
	private GLSurfaceViewContainer glSurfaceViewContainer;
	private boolean isGotyeChange;

	public enum Orientation {
		Horizontal, Vertical
	}

	private Orientation orientation;
	private String playState;
	private View loadingView;
	public TextView loadingText;
	public static boolean isPlay;

	private void findView() {

		menu = (LinearLayout) findViewById(R.id.menuId);
		line = (LinearLayout) findViewById(R.id.line);
		surfaceViewContainer = (RelativeLayout) findViewById(R.id.layout);
		loadingText = (TextView) findViewById(R.id.textview);
		loadingView = findViewById(R.id.loading);
		loadingView.setVisibility(View.VISIBLE);
		isDialogShow = true;
		glSurfaceViewContainer = new GLSurfaceViewContainer(this);
		KXTApplication.player.setSurfaceView(glSurfaceViewContainer.getSurfaceView());

		surfaceViewContainer.addView(glSurfaceViewContainer);

		viewPager = (MyViewPager2) findViewById(R.id.live_fragment);
		chatBtn = (ImageView) findViewById(R.id.chatId);
		serviceBtn = (ImageView) findViewById(R.id.serviceId);
		listBtn = (ImageView) findViewById(R.id.listId);
		functionBtn = (ImageView) findViewById(R.id.functionId);
		backBtn = (ImageView) findViewById(R.id.livebackId);
		fullBtn = (ImageView) findViewById(R.id.liveFullId);
		chatbg = (LinearLayout) findViewById(R.id.chatbgId);
		servicebg = (LinearLayout) findViewById(R.id.servicebgId);
		layout = (FrameLayout) findViewById(R.id.showLayout);
		listbg = (LinearLayout) findViewById(R.id.listbgId);
		functionbg = (LinearLayout) findViewById(R.id.functionbgId);
		params = (android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
		display = getWindowManager().getDefaultDisplay();
		chatBtn.setSelected(true);
		chatbg.setSelected(true);

		surfaceViewContainer.setOnClickListener(this);
		chatbg.setOnClickListener(this);
		listbg.setOnClickListener(this);
		servicebg.setOnClickListener(this);
		functionbg.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		fullBtn.setOnClickListener(this);
		initViewpager();
	}

	private void initViewpager() {
		viewPager.setOffscreenPageLimit(3);
		fragment_chat = new fragment_chat();
		// fragment_kf = new fragment_kefu(preferences.getString("kefu_url",
		// Constant.baseUrl + "/Index/kefu/?room_id=1"));
		fragment_kf = new fragment_kefu();
		Bundle bundle = new Bundle();
		bundle.putString("url", appinfo.getString("kefu_url", Constant.newBaseUrl + "/Appapi/Index/kefu/?room_id=1"));
		fragment_kf.setArguments(bundle);
		// fragment_hq = new fragment_web(preferences.getString("userlist_url",
		// Constant.baseUrl + "/room/userList"));
		fragment_hq = new fragment_web();
		bundle.putString("url1", appinfo.getString("userlist_url", "http://v8api.kxt.com/Quotes"));
		fragment_hq.setArguments(bundle);
		fragment_function = new fragment_function();
		fragments = new ArrayList<Fragment>();
		fragments.add(0, fragment_chat);
		fragments.add(1, fragment_kf);
		fragments.add(2, fragment_hq);
		fragments.add(3, fragment_function);
		adapter = new DemoAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				selecteTag(arg0);
				boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
				if (isOpen) {
					try {
						((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} catch (Exception e) {
					}
				}
				num = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
				if (isOpen) {
					try {
						((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} catch (Exception e) {
					}
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chatbgId:
			selecteTag(0);
			viewPager.setCurrentItem(0);
			break;
		case R.id.listbgId:
			selecteTag(2);
			viewPager.setCurrentItem(2);
			break;
		case R.id.servicebgId:
			selecteTag(1);
			viewPager.setCurrentItem(1);
			break;
		case R.id.functionbgId:
			selecteTag(3);
			viewPager.setCurrentItem(3);
			break;
		case R.id.livebackId:
			onBackPressed();
			break;
		case R.id.layout:
			timer.cancel();
			timer.purge();
			timer = new Timer();
			handler.sendEmptyMessage(2);
			setTimerTask();
			break;
		case R.id.liveFullId:
			if (!isFull) {
				GotyeLiveActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置横屏
				fullBtn.setSelected(false);
			} else {
				GotyeLiveActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
				fullBtn.setSelected(true);
			}

			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					GotyeLiveActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}
			}, 5 * 1000);
			break;
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		if (isFull) {
			GotyeLiveActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
			fullBtn.setSelected(true);
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					GotyeLiveActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}
			}, 5 * 1000);
		} else {
			finish();
		}
	}

	@Override
	public void finish() {
		Log.i("LiveActivity", "finish");
		// player.leave();
		// player.release(this);
		if (MainActivity.main != null && !MainActivity.main.isDestroyed()) {
			super.finish();
		} else {
			Intent intent = new Intent(GotyeLiveActivity.this, MainActivity.class);
			intent.putExtra("isLoadImg", false);
			startActivity(intent);
		}
		super.finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		setContentView(R.layout.activity_gotyelive);
		if (isFull) {
			findView3();
			fullBtn.setSelected(false);
		} else {
			findView2();
			fullBtn.setSelected(true);
		}
		isFull = !isFull;
		Log.i("info", "....1");
		timer.cancel();
		timer.purge();
		timer = new Timer();
		handler.sendEmptyMessage(2);
		setTimerTask();
	}

	private void findView3() {
		// TODO Auto-generated method stub
		surfaceViewContainer.removeAllViews();
		menu = (LinearLayout) findViewById(R.id.menuId);
		line = (LinearLayout) findViewById(R.id.line);
		surfaceViewContainer = (RelativeLayout) findViewById(R.id.layout);
		loadingText = (TextView) findViewById(R.id.textview);
		loadingView = findViewById(R.id.loading);
		loadingText.setText(dialogText);
		if (isDialogShow) {
			loadingView.setVisibility(View.VISIBLE);
		} else {
			loadingView.setVisibility(View.GONE);
		}

		if (isDialogTextShow) {
			loadingText.setVisibility(View.VISIBLE);
		} else {
			loadingText.setVisibility(View.GONE);
		}

		surfaceViewContainer.addView(glSurfaceViewContainer);

		viewPager = (MyViewPager2) findViewById(R.id.live_fragment);
		chatBtn = (ImageView) findViewById(R.id.chatId);
		serviceBtn = (ImageView) findViewById(R.id.serviceId);
		listBtn = (ImageView) findViewById(R.id.listId);
		functionBtn = (ImageView) findViewById(R.id.functionId);
		backBtn = (ImageView) findViewById(R.id.livebackId);
		fullBtn = (ImageView) findViewById(R.id.liveFullId);
		chatbg = (LinearLayout) findViewById(R.id.chatbgId);
		servicebg = (LinearLayout) findViewById(R.id.servicebgId);
		layout = (FrameLayout) findViewById(R.id.showLayout);
		listbg = (LinearLayout) findViewById(R.id.listbgId);
		functionbg = (LinearLayout) findViewById(R.id.functionbgId);
		params = (android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
		display = getWindowManager().getDefaultDisplay();
		chatBtn.setSelected(true);
		chatbg.setSelected(true);

		surfaceViewContainer.setOnClickListener(this);
		chatbg.setOnClickListener(this);
		listbg.setOnClickListener(this);
		servicebg.setOnClickListener(this);
		functionbg.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		fullBtn.setOnClickListener(this);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				selecteTag(arg0);
				boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
				if (isOpen) {
					try {
						((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} catch (Exception e) {
					}
				}
				num = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
				if (isOpen) {
					try {
						((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} catch (Exception e) {
					}
				}
			}
		});
	}

	private void findView2() {
		// TODO Auto-generated method stub
		surfaceViewContainer.removeAllViews();
		surfaceViewContainer = (RelativeLayout) findViewById(R.id.layout);
		loadingText = (TextView) findViewById(R.id.textview);
		loadingView = findViewById(R.id.loading);
		if (isDialogShow)
			loadingView.setVisibility(View.VISIBLE);
		else
			loadingView.setVisibility(View.GONE);

		loadingText.setText(dialogText);
		if (isDialogTextShow)
			loadingText.setVisibility(View.VISIBLE);
		else
			loadingText.setVisibility(View.GONE);
		surfaceViewContainer.addView(glSurfaceViewContainer);

		backBtn = (ImageView) findViewById(R.id.livebackId);
		fullBtn = (ImageView) findViewById(R.id.liveFullId);
		params = (android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
		display = getWindowManager().getDefaultDisplay();

		surfaceViewContainer.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		fullBtn.setOnClickListener(this);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				selecteTag(arg0);
				boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
				if (isOpen) {
					try {
						((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} catch (Exception e) {
					}
				}
				num = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
				if (isOpen) {
					try {
						((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					} catch (Exception e) {
					}
				}
			}
		});

	}

	/**
	 * 选择tag,tag背景变化
	 * 
	 * @param arg0
	 *            tag下标
	 */
	private void selecteTag(int arg0) {
		switch (arg0) {
		case 0:
			chatBtn.setSelected(true);
			listBtn.setSelected(false);
			serviceBtn.setSelected(false);
			functionBtn.setSelected(false);
			chatbg.setSelected(true);
			listbg.setSelected(false);
			servicebg.setSelected(false);
			functionbg.setSelected(false);
			break;
		case 1:
			chatBtn.setSelected(false);
			listBtn.setSelected(false);
			serviceBtn.setSelected(true);
			functionBtn.setSelected(false);
			chatbg.setSelected(false);
			listbg.setSelected(false);
			servicebg.setSelected(true);
			functionbg.setSelected(false);
			break;
		case 2:
			chatBtn.setSelected(false);
			listBtn.setSelected(true);
			serviceBtn.setSelected(false);
			functionBtn.setSelected(false);
			chatbg.setSelected(false);
			listbg.setSelected(true);
			servicebg.setSelected(false);
			functionbg.setSelected(false);
			break;
		case 3:
			chatBtn.setSelected(false);
			listBtn.setSelected(false);
			serviceBtn.setSelected(false);
			functionBtn.setSelected(true);
			chatbg.setSelected(false);
			listbg.setSelected(false);
			servicebg.setSelected(false);
			functionbg.setSelected(true);
			break;

		default:
			break;
		}
	}

	class DemoAdapter extends FragmentPagerAdapter {
		public DemoAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			if (object instanceof fragment_function)
				return POSITION_NONE;
			return POSITION_UNCHANGED;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return fragment_chat;
			case 1:
				return fragment_kf;
			case 2:
				return fragment_hq;
			case 3:
				return fragment_function;
			}
			return null;
		}

		@Override
		public int getCount() {
			// 多少页
			return fragments.size();
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

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				backBtn.setVisibility(View.GONE);
				fullBtn.setVisibility(View.GONE);
				break;
			case 2:
				backBtn.setVisibility(View.VISIBLE);
				fullBtn.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}

		};
	};

	/**
	 * 重力感应监听者
	 */

	/**
	 * 定时隐藏后退按钮和全屏按钮
	 */
	private void setTimerTask() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(1);
			}
		}, 2 * 1000);
	}

	@Override
	protected void onStop() {
		Log.i("LiveActivity", "stop");
		// if (fragment_chat != null)
		// fragment_chat.onDestroy();
		KXTApplication.player.stop();
		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.i("LiveActivity", "pause");
		isChange = true;
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i("LiveActivity", "restart");
		super.onRestart();

		loadingView.setVisibility(View.VISIBLE);
		isDialogShow = true;
		fragment_kf = new fragment_kefu();
		Bundle bundle = new Bundle();
		bundle.putString("url", appinfo.getString("kefu_url", Constant.newBaseUrl + "/Appapi/Index/kefu"));
		fragment_kf.setArguments(bundle);
		fragment_hq = new fragment_web();
		bundle.putString("url1", appinfo.getString("userlist_url", Constant.newBaseUrl + "/Appapi/Index/userlist"));
		fragment_hq.setArguments(bundle);
		fragment_function = new fragment_function();
		fragments = new ArrayList<Fragment>();
		fragments.add(0, fragment_chat);
		fragments.add(1, fragment_kf);
		fragments.add(2, fragment_hq);
		fragments.add(3, fragment_function);
		adapter = new DemoAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(num);

		bindService();
	}

	@Override
	protected void onResume() {
		Log.i("LiveActivity", "resume");
		// mLivePlayer.setDataSource(url);
		KXTApplication.player.setListener(this);
		KXTApplication.player.play();
		fragment_chat = new fragment_chat();
		if (isChange) {
			isChange = false;
			fragment_function = new fragment_function();
		}
		adapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.i("LiveActivity", "destory");
		unregisterReceiver(receiver);
		unregisterReceiver(mBatInfoReceiver);
		KXTApplication.core.clearAuth();
		KXTApplication.IsOut = true;
		live = null;
		stopService(intent);
		super.onDestroy();
	}

	public fragment_chat getFragment() {
		return fragment_chat;
	}

	class CallPhoneBroadcaseReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			System.out.println("action" + intent.getAction());
			// 如果是去电
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			} else {
				// 查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
				// 如果我们想要监听电话的拨打状况，需要这么几步 :
				/*
				 * 第一：获取电话服务管理器TelephonyManager manager =
				 * this.getSystemService(TELEPHONY_SERVICE);
				 * 第二：通过TelephonyManager注册我们要监听的电话状态改变事件。manager.listen(new
				 * MyPhoneStateListener(),
				 * PhoneStateListener.LISTEN_CALL_STATE);
				 * 这里的PhoneStateListener.LISTEN_CALL_STATE就是我们想要
				 * 监听的状态改变事件，初次之外，还有很多其他事件哦。 第三步：通过extends
				 * PhoneStateListener来定制自己的规则。将其对象传递给第二步作为参数。
				 * 第四步：这一步很重要，那就是给应用添加权限。android.permission.READ_PHONE_STATE
				 */

				TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
				tm.listen(listener2, PhoneStateListener.LISTEN_CALL_STATE);
				// 设置一个监听器
			}
		}
	}

	PhoneStateListener listener2 = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// 注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("挂断");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("接听");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("响铃:来电号码" + incomingNumber);
				live.finish();
				break;
			}
		}
	};
	private BroadcastReceiver mBatInfoReceiver;
	private Intent intent;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void isChange(boolean ischange) {
		// TODO Auto-generated method stub
		if (ischange) {
			functionBtn.setBackground(getResources().getDrawable(R.drawable.back));
		} else {
			functionBtn.setBackground(getResources().getDrawable(R.drawable.selector_live_function));
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void changeTitle() {
		// TODO Auto-generated method stub
		functionBtn.setBackground(getResources().getDrawable(R.drawable.selector_live_function));
	}

	@Override
	public void videoGone() {
		// TODO Auto-generated method stub
		// findViewById(R.id.showLayout).setVisibility(View.GONE);
	}

	@Override
	public void videoVisible() {
		// TODO Auto-generated method stub
		// findViewById(R.id.showLayout).setVisibility(View.VISIBLE);
	}

	private void screenOffOrOn() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		// 屏幕灭屏广播
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		// 屏幕亮屏广播
		filter.addAction(Intent.ACTION_SCREEN_ON);
		// 屏幕解锁广播
		filter.addAction(Intent.ACTION_USER_PRESENT);
		// // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
		// // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
		// // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
		// filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

		mBatInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				String action = intent.getAction();
				if (Intent.ACTION_SCREEN_ON.equals(action)) {
					// Log.d("info", "screen on 打开声音");
					// player.audioSet(false);
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					Log.d("info", "screen off 关闭声音");
					KXTApplication.player.setMute(true);
				} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
					Log.d("info", "screen unlock 打开声音");
					KXTApplication.player.setMute(false);
				}
			}
		};
		registerReceiver(mBatInfoReceiver, filter);
	}

	@Override
	public void changeDirection() {
		isGotyeChange = true;

		if (orientation == Orientation.Horizontal) {
			orientation = Orientation.Vertical;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			orientation = Orientation.Horizontal;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		playState = loadingText.getText().toString();
	}

	@Override
	public void onLiveStateChange(String roomId, GLPlayer.LiveState liveState) {
		Toast.makeText(this, "主播状态改变 : " + liveState, Toast.LENGTH_SHORT).show();
		if (liveState == GLPlayer.LiveState.STOPPED) {
			isPlay = false;
			playState = "直播结束";
			dialogText = playState;
			loadingText.setText(playState);
			loadingView.setVisibility(View.GONE);
			isDialogShow = false;
		} else {
			isPlay = true;
			playState = "";
			loadingText.setText(playState);
			loadingView.setVisibility(View.VISIBLE);
			isDialogShow = true;
		}

	}

	@Override
	public void onPlayerDisconnect(String roomId) {
		loadingView.setVisibility(View.VISIBLE);
		isDialogShow = true;
		isPlay = false;
		playState = "网络异常";
		dialogText = playState;
		loadingText.setText(playState);
	}

	@Override
	public void onPlayerReconnect(String roomId) {
		loadingView.setVisibility(View.VISIBLE);
		isDialogShow = true;
		dialogText = "正在连接...";
		loadingText.setText("正在连接...");
		isPlay = false;

	}

	@Override
	public void onPlayerStart(String roomId) {
		loadingView.setVisibility(View.GONE);
		isDialogShow = false;
		isDialogTextShow = false;
		dialogText = "";
		loadingText.setText("");

		isPlay = true;
	}

	@Override
	public void onPlayerError(String s, int i) {
		isPlay = false;
		loadingView.setVisibility(View.GONE);
		isDialogShow = false;
		loadingText.setVisibility(View.VISIBLE);
		isDialogTextShow = true;
		switch (i) {
		case Code.LIVE_NOT_STARTTEDYET:
			loadingText.setText("直播未开始");
			dialogText = "直播未开始";
			break;
		case Code.NETWORK_DISCONNECT:
			loadingText.setText("网络断开");
			dialogText = "网络断开";
			break;
		case Code.GET_LIVE_STATE_FAILED:
			loadingText.setText("获取直播状态失败");
			dialogText = "获取直播状态失败";
			break;
		case Code.INVALID_TOKEN:
			loadingText.setText("token无效");
			dialogText = "token无效";
			break;
		case Code.ILLEGAL_STATE:
			loadingText.setText("当前状态不能进行该操作");
			dialogText = "当前状态不能进行该操作";
			break;
		case Code.FAILED:
			loadingText.setText("失败");
			dialogText = "失败";
			break;
		case Code.GET_LIVE_URL_FAILED:
			loadingText.setText("获取直播URL失败");
			dialogText = "获取直播URL失败";
			break;

		}

	}

	@Override
	public void close() {
		finish();
	}

	/**
	 * 绑定聊天socket
	 */
	private void bindService() {
		if (!(ServiceUtil.isServiceRunning(this, ChatService.class.getName()))) {
			intent = new Intent(GotyeLiveActivity.this, ChatService.class);
			// bindService(intent, mConnection,Service.START_STICKY);
			startService(intent);
		}
	}
}

interface VideoGone {
	public void videoGone();

	public void videoVisible();
}
