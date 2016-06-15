package com.jyh.hjtzdxt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.jyh.hjtzdxt.fragment.fragment_data;
import com.jyh.hjtzdxt.fragment.fragment_flash;
import com.jyh.hjtzdxt.fragment.fragment_gonggao;
import com.jyh.hjtzdxt.fragment.fragment_hq;
import com.jyh.hjtzdxt.fragment.fragment_kecheng;
import com.jyh.hjtzdxt.fragment.fragment_shuju;
import com.jyh.hjtzdxt.tool.DisplayUtilJYH;

/*
 * 直播室-功能界面
 */
public class _Activity extends FragmentActivity  {
	private FrameLayout layout;
	public HorizontalScrollView mTouchView;

	private fragment_flash fragment_flash;
	private fragment_data fragment_data;
	private fragment_kecheng fragment_kecheng;
	private fragment_gonggao fragment_gonggao;
	private fragment_shuju fragment_shuju;
	private fragment_hq fragment_hq_hq;
	private FragmentTransaction transaction;
	private String from;
	private FragmentManager fm;
	private int type;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.BrowserThemeDefault);
		setContentView(R.layout.activity_);
		layout = (FrameLayout) findViewById(R.id.layout);
		fm = getSupportFragmentManager();
		transaction = fm.beginTransaction();

		setDialogStyle();

		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL,
				LayoutParams.FLAG_NOT_TOUCH_MODAL); // ...but notify us that it
													// happened.
		getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		// Note that flag changes
		// must happen *before* the
		// content view is set.

		type = getIntent().getIntExtra("type", 0);
		from = getIntent().getStringExtra("from");
		hindfragment();
		switch (type) {
		case 1:
			// 快讯
			if (fragment_flash == null) {
				fragment_flash = new fragment_flash();
				Bundle bundle = new Bundle();
				bundle.putString("from", from);
				fragment_flash.setArguments(bundle);
				transaction.add(R.id.layout, fragment_flash);
			}
			transaction.show(fragment_flash);
			break;
		case 2:
			// 行情
			if (fragment_hq_hq == null) {
				fragment_hq_hq = new fragment_hq();
				try {
					Bundle bundle = new Bundle();
					bundle.putString("from", from);
					fragment_hq_hq.setArguments(bundle);
				} catch (Exception e) {
					// TODO: handle exception
				}
				transaction.add(R.id.layout, fragment_hq_hq);
			}
			transaction.show(fragment_hq_hq);
			break;
		case 3:
			// 日历
			if (fragment_data == null) {
				fragment_data = new fragment_data();
				Bundle bundle = new Bundle();
				bundle.putString("from", from);
				fragment_data.setArguments(bundle);
				transaction.add(R.id.layout, fragment_data);
			}
			transaction.show(fragment_data);
			break;
		case 4:
			// 数据
			if (fragment_shuju == null) {
				fragment_shuju = new fragment_shuju();
				transaction.add(R.id.layout, fragment_shuju);
			}
			transaction.show(fragment_shuju);
			break;
		case 5:
			// 课程
			if (fragment_kecheng == null) {
				fragment_kecheng = new fragment_kecheng();
				transaction.add(R.id.layout, fragment_kecheng);
			}
			transaction.show(fragment_kecheng);
			break;
		case 6:
			// 公告
			if (fragment_gonggao == null) {
				fragment_gonggao = new fragment_gonggao();
				transaction.add(R.id.layout, fragment_gonggao);
			}
			transaction.show(fragment_gonggao);
			break;
		case 0:
			// 数据错误
			onBackPressed();
			break;
		}
		transaction.commit();
	}

	private void hindfragment() {
		// TODO Auto-generated method stub
		if (fragment_data != null)
			transaction.hide(fragment_data);
		if (fragment_flash != null)
			transaction.hide(fragment_flash);
		if (fragment_hq_hq != null)
			transaction.hide(fragment_hq_hq);
		if (fragment_data != null)
			transaction.hide(fragment_data);
		if (fragment_shuju != null)
			transaction.hide(fragment_shuju);
		if (fragment_gonggao != null)
			transaction.hide(fragment_gonggao);
	}

	@SuppressWarnings("deprecation")
	private void setDialogStyle() {
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		float size = DisplayUtilJYH.getDpi(this);
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) ((d.getHeight() - (float) 40 * size - DisplayUtilJYH
				.getStatuBarHeight(this)) / 2.75 * 1.75); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度

		getWindow().setAttributes(p); // 设置生效
		getWindow().setGravity(Gravity.BOTTOM); // 设置居中
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			hindfragment();
			finish();
			return true;
		}
		return super.onTouchEvent(event);
	}

	public interface ischangeTitle {
		public void changeTitle();
	}

	@Override
	public void finish() {
		if (GotyeLiveActivity.live != null) {
			((ischangeTitle) (GotyeLiveActivity.live)).changeTitle();
		}
		super.finish();
	}

}
