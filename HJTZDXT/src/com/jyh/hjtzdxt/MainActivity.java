package com.jyh.hjtzdxt;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.fragment.fragment_data;
import com.jyh.hjtzdxt.fragment.fragment_flash;
import com.jyh.hjtzdxt.fragment.fragment_hq;
import com.jyh.hjtzdxt.fragment.fragment_self;
import com.jyh.hjtzdxt.fragment.fragment_self.OnFragmentListener;
import com.jyh.hjtzdxt.fragment.fragment_zb;
import com.jyh.hjtzdxt.service.ImageService;
import com.jyh.hjtzdxt.socket.NetworkCenter;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends FragmentActivity implements OnClickListener, OnFragmentListener {
	private fragment_hq fragment_kxthq;// 快讯通行情
	private fragment_data data;
	private fragment_self fragment_self;
	private ImageView imgdata, imgflash, imghq, imgself, imgjw;
	private fragment_zb fragment_zb;
	private fragment_flash fragment_flash;
	private KXTApplication application;
	private LinearLayout main_ll_flash, main_ll_yw, main_ll_hq, main_ll_rl, main_ll_self, main_zt_color;
	private long mExitTime;
	private FragmentManager fragmentManager;
	private int cuntenpage = 0;
	public static Activity main;
	// 手指上下滑动时的最小速度
	private static final int YSPEED_MIN = 1000;

	// 手指向右滑动时的最小距离
	private static final int XDISTANCE_MIN = 150;
	// 手指向上滑或下滑时的最小距离
	private static final int YDISTANCE_MIN = 100;

	// 记录手指按下时的横坐标。
	private float xDown;

	// 记录手指按下时的纵坐标。
	private float yDown;

	// 记录手指移动时的横坐标。
	private float xMove;

	// 记录手指移动时的纵坐标。
	private float yMove;

	// 用于计算手指滑动的速度。
	private VelocityTracker mVelocityTracker;
	private boolean isSend = false;
	public HorizontalScrollView mTouchView, mTouchView1;
	ImageView leftOk, leftNo, rightOk, rightNo;
	private Timer timer;
	Handler handler;
	private FragmentTransaction transaction;
	private SharedPreferences preferences1;
	private Timer Checktimer;
	private boolean isCheck = true;
	private boolean mBound;

	Handler mianHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 10:
				if (isCheck) {
					isCheck = false;
				}
				break;

			default:
				break;
			}
		};
	};
	private boolean maincolor = true;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		preferences1 = getSharedPreferences("isHQCenter", Context.MODE_PRIVATE);
		this.setTheme(R.style.BrowserThemeDefault);
		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		main = this;

		cuntenpage = getIntent().getIntExtra("viewpager", 0);
		InitFind();
		fragmentManager = getSupportFragmentManager();
		Intent intent = getIntent();
		String data = intent.getStringExtra("data");
		String enter = intent.getStringExtra("enter");
		String type = intent.getStringExtra("type");
		if (null != data && !data.equals("")) {
			Notifacation();
		} else if (null != enter && !enter.equals("")) {
			setTabSelection(4);
		} else if (null != type && type.contains("new")) {
			setTabSelection(1);
		} else if (null != type && type.contains("dian")) {
			setTabSelection(0);
			handler.sendEmptyMessage(2);
		} else if (null != type && type.contains("video")) {
			setTabSelection(0);
			handler.sendEmptyMessage(3);
		} else if (null != type && type.contains("flash")) {
			setTabSelection(0);
			handler.sendEmptyMessage(1);
		} else if (cuntenpage != 0) {
			setTabSelection(cuntenpage);
		} else
			setTabSelection(0);
		Checktimer = new Timer();

		Checktimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (NetworkCenter.checkNetworkConnection(MainActivity.this)) {
					isCheck = true;
				} else {
					mianHandler.sendEmptyMessage(10);
				}
			}
		}, 0, 5 * 1000);

		if (intent.getBooleanExtra("isLoadImg", true)) {
			startService(new Intent(this, ImageService.class));
		}
	}

	@SuppressLint("NewApi")
	private void setTabSelection(int index) {

		// 重置按钮
		resetBtn();
		// 开启一个Fragment事务
		transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		try {
			switch (index) {
			case 0:
				// 当点击了消息tab时，改变控件的图片和文字颜色
				imgflash.setSelected(true);
				cuntenpage = 0;
				if (fragment_zb == null) {
					// 如果MessageFragment为空，则创建一个并添加到界面上
					fragment_zb = new fragment_zb();
					transaction.add(R.id.frame_content, fragment_zb);
				} else {
					// 如果MessageFragment不为空，则直接将它显示出来
					transaction.show(fragment_zb);
				}
				main_zt_color.setBackgroundResource(R.drawable.live_bg);
				maincolor = true;
				break;
			case 1:
				// 当点击了消息tab时，改变控件的图片和文字颜色
				imgjw.setSelected(true);
				cuntenpage = 1;
				if (fragment_flash == null) {
					// 如果MessageFragment为空，则创建一个并添加到界面上
					fragment_flash = new fragment_flash();
					transaction.add(R.id.frame_content, fragment_flash);
				} else {
					// 如果MessageFragment不为空，则直接将它显示出来
					if (fragment_flash.error && KXTApplication.isHaveNet) {
						fragment_flash = new fragment_flash();
						transaction.add(R.id.frame_content, fragment_flash);
					} else
						transaction.show(fragment_flash);
				}
				if (maincolor) {
					main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
					maincolor = false;
				}
				break;
			case 2:
				// 当点击了动态tab时，改变控件的图片和文字颜色
				imghq.setSelected(true);
				cuntenpage = 2;
				if (fragment_kxthq == null) {
					fragment_kxthq = new fragment_hq();
					transaction.add(R.id.frame_content, fragment_kxthq);
				} else {
					if (fragment_kxthq.error && KXTApplication.isHaveNet) {
						fragment_kxthq = new fragment_hq();
						transaction.add(R.id.frame_content, fragment_kxthq);
					} else
						transaction.show(fragment_kxthq);
				}
				if (maincolor) {
					main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
					maincolor = false;
				}
				
				preferences1.edit().putBoolean("isChange", true).commit();
				
				break;
			case 3:
				// 当点击了设置tab时，改变控件的图片和文字颜色
				application.getMmp().put("4", "4");
				imgdata.setSelected(true);
				cuntenpage = 3;
				if (data == null) {
					// 如果SettingFragment为空，则创建一个并添加到界面上
					data = new fragment_data();
					transaction.add(R.id.frame_content, data);
				} else {
					Log.i("hehe", data.error + " " + KXTApplication.isHaveNet);
					// 如果SettingFragment不为空，则直接将它显示出来
					if (data.error && KXTApplication.isHaveNet) {
						data = new fragment_data();
						transaction.add(R.id.frame_content, data);
					} else
						transaction.show(data);
				}
				if (maincolor) {
					main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
					maincolor = false;
				}
				break;
			case 4:
				imgself.setSelected(true);
				cuntenpage = 4;
				if (fragment_self == null) {
					// 如果SettingFragment为空，则创建一个并添加到界面上
					fragment_self = new fragment_self();
					transaction.add(R.id.frame_content, fragment_self);
				} else {
					// 如果SettingFragment不为空，则直接将它显示出来
					transaction.show(fragment_self);
				}
				if (maincolor) {
					main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
					maincolor = false;
				}
				break;
			}
			// transaction.commitAllowingStateLoss();
			transaction.commit();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void resetBtn() {
		imgdata.setSelected(false);
		imgflash.setSelected(false);
		imghq.setSelected(false);
		imgself.setSelected(false);
		imgjw.setSelected(false);
	}

	@SuppressLint("NewApi")
	private void hideFragments(FragmentTransaction transaction) {
		if (fragment_zb != null) {
			transaction.hide(fragment_zb);
		}
		if (fragment_kxthq != null) {
			transaction.hide(fragment_kxthq);
		}
		if (fragment_flash != null) {
			transaction.hide(fragment_flash);
		}
		if (fragment_self != null) {
			transaction.hide(fragment_self);
		}
		if (data != null) {
			transaction.hide(data);
		}
	}

	private void Notifacation() {
		// TODO Auto-generated method stub
		imgdata.setSelected(true);
		// flash = new fragment_flash();
		// fragment_flash_main = new fragment_flash_main();
		data = new fragment_data();
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		// 替换当前的页面
		fragmentTransaction.replace(R.id.frame_content, data);
		// 事务管理提交
		fragmentTransaction.commitAllowingStateLoss();
		// fragmentTransaction.commit();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private void clickAtBtn() {
		main_ll_flash.setOnClickListener(this);
		main_ll_hq.setOnClickListener(this);
		main_ll_rl.setOnClickListener(this);
		main_ll_self.setOnClickListener(this);
		main_ll_yw.setOnClickListener(this);
	}

	private void InitFind() {
		main_zt_color = (LinearLayout) findViewById(R.id.main_zt_color);
		main_ll_flash = (LinearLayout) findViewById(R.id.mian_ll_flash);
		main_ll_hq = (LinearLayout) findViewById(R.id.mian_ll_hq);
		main_ll_rl = (LinearLayout) findViewById(R.id.mian_ll_rl);
		main_ll_self = (LinearLayout) findViewById(R.id.mian_ll_self);
		main_ll_yw = (LinearLayout) findViewById(R.id.mian_ll_yw);
		imgdata = (ImageView) findViewById(R.id.imgdata);
		imgflash = (ImageView) findViewById(R.id.imgflash);
		imghq = (ImageView) findViewById(R.id.imghq);
		imgself = (ImageView) findViewById(R.id.imgself);
		imgjw = (ImageView) findViewById(R.id.imgjw);
		main_zt_color.setBackgroundColor(Color.parseColor("#1677E0"));
		application = (KXTApplication) getApplication();
		application.addAct(this);
		clickAtBtn();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mian_ll_flash:
			setTabSelection(0);
			break;
		case R.id.mian_ll_yw:
			setTabSelection(1);
			break;
		case R.id.mian_ll_hq:
			setTabSelection(2);
			break;
		case R.id.mian_ll_rl:
			setTabSelection(3);
			break;
		case R.id.mian_ll_self:
			setTabSelection(4);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				application.exitAppAll();

			}
			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFragmentAction() {
		// TODO Auto-generated method stub

	}

	/**
	 * 创建VelocityTracker对象，并将触摸界面的滑动事件加入到VelocityTracker当中。
	 *
	 * @param event
	 *
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	/**
	 *
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getYVelocity();
		return Math.abs(velocity);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getRawX();
			yDown = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			xMove = event.getRawX();
			yMove = event.getRawY();
			// 滑动的距离
			int distanceX = (int) (xMove - xDown);
			int distanceY = (int) (yMove - yDown);
			// 获取顺时速度
			int ySpeed = getScrollVelocity();
			// 关闭Activity需满足以下条件：
			// 1.x轴滑动的距离>XDISTANCE_MIN
			// 2.y轴滑动的距离在YDISTANCE_MIN范围内
			// 3.y轴上（即上下滑动的速度）<XSPEED_MIN，如果大于，则认为用户意图是在上下滑动而非左滑结束Activity
			if (ySpeed > 100) {
			} else if (ySpeed < 100) {
				if (distanceX > XDISTANCE_MIN && (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN) && ySpeed < YSPEED_MIN) {
				} else if (distanceX < -XDISTANCE_MIN && (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN) && ySpeed < YSPEED_MIN) {
					if (cuntenpage == 3) {
						if (!isSend) {
							isSend = true;
							Intent intent = new Intent();
							intent.setAction("分类跳转");
							sendBroadcast(intent);
						}
					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			isSend = false;
			recycleVelocityTracker();
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			}
		} catch (Exception ex) {
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		main = null;
		super.onDestroy();
		if (timer != null) {
			timer.purge();
			timer.cancel();
		}
		application.ischange = true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	public interface FragmentActivityResult {
		public void OnActivityResult(int requestCode, int resultCode, Intent data);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		// if (arg1 == 102)
		// ((FragmentActivityResult) fragment_flash_main).OnActivityResult(
		// arg0, arg1, arg2);
	}
}
