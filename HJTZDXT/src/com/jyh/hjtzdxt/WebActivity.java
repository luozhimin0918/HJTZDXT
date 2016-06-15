package com.jyh.hjtzdxt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jyh.hjtzdxt.tool.DisplayUtilJYH;

public class WebActivity extends Activity {
	private WebView webView;
	private TextView tv;
	private SharedPreferences preferences;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getSharedPreferences("setup",
				Context.MODE_PRIVATE);
			setTheme(R.style.BrowserThemeDefault);
		setContentView(R.layout.activity_hq);

		setDialogStyle();

		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL,
				LayoutParams.FLAG_NOT_TOUCH_MODAL); // ...but notify us that it
													// happened.
		getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		// Note that flag changes
		// must happen *before* the
		// content view is set.
		String url = getIntent().getStringExtra("url");
		webView = (WebView) findViewById(R.id.webView);
		tv = (TextView) findViewById(R.id.tvId);
		webView.setVisibility(View.VISIBLE);
		tv.setVisibility(View.GONE);
		WebSettings webSeting = webView.getSettings();
		webSeting.setJavaScriptEnabled(true);
		webSeting.setLoadsImagesAutomatically(true);
		webSeting.setLoadWithOverviewMode(true);
		webSeting.setUseWideViewPort(true);
		webSeting.setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				try {
					findViewById(R.id.dialog_view).setVisibility(View.GONE);
				} catch (Exception e) {
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}

		});
		webView.loadUrl(url);
		findViewById(R.id.img).startAnimation(
				AnimationUtils.loadAnimation(this, R.anim.loading_animation));
	}

	private void setDialogStyle() {
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		float size = DisplayUtilJYH.getDpi(this);
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) ((d.getHeight() - (float) 40 * size-DisplayUtilJYH.getStatuBarHeight(this)) / 2.75 * 1.75); // 高度设置为屏幕的1.0
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

	public interface ischangeTitle {
		public void changeTitle();
	}

	@Override
	public void finish() {
		if (GotyeLiveActivity.live != null) {
			((ischangeTitle) (GotyeLiveActivity.live)).changeTitle();
		} 
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}

}
