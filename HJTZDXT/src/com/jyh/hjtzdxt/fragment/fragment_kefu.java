package com.jyh.hjtzdxt.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jyh.hjtzdxt.R;
import com.jyh.hjtzdxt.customtool.MyWebView;

public class fragment_kefu extends Fragment {
	private String uri;
	private MyWebView webView;
	private View tv;

	// private PackageInfo packageInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
			uri = getArguments().getString("url");
		// try {
		// packageInfo =
		// getActivity().getPackageManager().getPackageInfo("com.tencent.mobileqq",
		// 0);
		// } catch (NameNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// packageInfo = null;
		// }
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_kefu, null);
		tv = (View) view.findViewById(R.id.tvId);
		tv.setVisibility(View.GONE);
		webView = (MyWebView) view.findViewById(R.id.webView1);
		webView.setVisibility(View.VISIBLE);
		// if (packageInfo == null) {
		// webView.setClickable(false);
		// } else
		// webView.setClickable(true);
		view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
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
				if (url.contains("mqqwpa")) {
					v.setVisibility(View.GONE);
				}else
				v.setVisibility(View.VISIBLE);
				// view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView v, int errorCode, String description, String failingUrl) {
				v.setVisibility(View.GONE);
				v.loadUrl(uri);

				// webView.setVisibility(View.GONE);
				// tv.setVisibility(View.VISIBLE);
				// view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
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
		webView.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
		webView.getSettings().setBuiltInZoomControls(true);//
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
		webView.removeJavascriptInterface("searchBoxJavaBridge_");
		webView.addJavascriptInterface(new isQQ(), "QQ");
		webView.loadUrl(uri);
		// view.findViewById(R.id.img).startAnimation(AnimationUtils.loadAnimation(getActivity(),
		// R.anim.loading_animation));
		// Log.i("kefu", "oncreateView");
		return view;
	}

	public fragment_kefu() {
		super();
	}

	class isQQ {
		@JavascriptInterface
		public boolean isAppInstalled() {
			PackageInfo packageInfo;
			try {
				packageInfo = getActivity().getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
			} catch (Exception e) {
				packageInfo = null;
				e.printStackTrace();
			}
			if (packageInfo == null) {
				Log.i("hehe", "ddddddddddddddddd");
				return false;
			} else {
				Log.i("hehe", "eeeeeeeeeeeeeeeee");
				return true;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		webView.loadUrl(uri);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("url", uri);
	}

}
