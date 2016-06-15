package com.jyh.hjtzdxt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WebActivity2 extends Activity {
	private WebView webView;
	private TextView tv;
	private SharedPreferences preferences, appinfo;

	private LinearLayout title;
	private TextView title_tv;

	private String summary_url;
	private boolean isKF;

	private View view;
	private String url;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
		appinfo = getSharedPreferences("appinfo", Context.MODE_PRIVATE);
		summary_url = appinfo.getString("summary_url", "http://appapi.v8.kxt.com/room/summary");
		url = getIntent().getStringExtra("url");

		setTheme(R.style.BrowserThemeDefault);

		if (!summary_url.equals(url)) {
			isKF = true;
			setContentView(R.layout.activity_hq2);
		} else {
			isKF = false;
			setContentView(R.layout.activity_hq);
		}

		title = (LinearLayout) findViewById(R.id.title);
		title_tv = (TextView) findViewById(R.id.title_tv);
		title.setVisibility(View.GONE);

		if (null != getIntent().getStringExtra("from") && "main".equals(getIntent().getStringExtra("from"))) {
			title.setVisibility(View.VISIBLE);
			findViewById(R.id.self_fk_img).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}

		if (null != getIntent().getStringExtra("title")) {
			title_tv.setText(getIntent().getStringExtra("title"));
		}

		view = findViewById(R.id.dialog_view);
		view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
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
		webView.removeJavascriptInterface("searchBoxJavaBridge_");
		if (isKF)
			webView.addJavascriptInterface(new isQQ(), "QQ");
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// Log.i("kefu", "shouldOverrideUrlLoading" + url);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView v, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(v, url);
				// view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView v, int errorCode, String description, String failingUrl) {
				// view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
				if (isKF) {
					v.loadUrl(url);
				} else {
					tv.setVisibility(View.VISIBLE);
					webView.setVisibility(View.GONE);
				} 
			}

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
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
		});

		webView.loadUrl(url);

		// findViewById(R.id.img).startAnimation(AnimationUtils.loadAnimation(this,
		// R.anim.loading_animation));
	}

	class isQQ {
		@JavascriptInterface
		public boolean isAppInstalled() {
			PackageInfo packageInfo;
			try {
				packageInfo = getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
			} catch (Exception e) {
				packageInfo = null;
				e.printStackTrace();
			}
			if (packageInfo == null) {
				// System.out.println("没有安装");
				return false;
			} else {
				// System.out.println("已经安装");
				return true;
			}
		}
	}

}
