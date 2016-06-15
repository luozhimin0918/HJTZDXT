package com.jyh.hjtzdxt.fragment;

import java.lang.ref.WeakReference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jyh.hjtzdxt.R;

public class fragment_flash extends Fragment {
	private SharedPreferences preferences;
	private View view;
	protected WeakReference<View> mRootView;

	private String from;
	private WebView webView;
	private TextView tv;
	private String url;
	public boolean error;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getArguments() != null)
			from = getArguments().getString("from");
		view = inflater.inflate(R.layout.activity_hq, null);
		InitFind();
		return view;
	}

	private void InitFind() {
		// TODO Auto-generated method stub
		webView = (WebView) view.findViewById(R.id.webView);
		tv = (TextView) view.findViewById(R.id.tvId);
		view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
//		webView.setVisibility(View.VISIBLE);
//		tv.setVisibility(View.GONE);
		WebSettings webSeting = webView.getSettings();
		webSeting.setJavaScriptEnabled(true);
		webSeting.setLoadsImagesAutomatically(true);
		webSeting.setLoadWithOverviewMode(true);
		webSeting.setUseWideViewPort(true);
		webSeting.setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView v, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(v, url);
//				view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView v, int errorCode, String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(v, errorCode, description, failingUrl);
				error = true;
				webView.setVisibility(View.GONE);
				tv.setVisibility(View.VISIBLE);
//				view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}

		});

		preferences = getActivity().getSharedPreferences("appinfo", getActivity().MODE_PRIVATE);
		url = preferences.getString("alters_url", "http://v8api.kxt.com/alerts");
		webView.loadUrl(url);
//		view.findViewById(R.id.img).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.loading_animation));
	}

}
