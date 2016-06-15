package com.jyh.hjtzdxt.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.jyh.hjtzdxt.R;

public class fragment_web extends Fragment {
	private String uri;
	private WebView webView;
	private TextView tv;

//	public fragment_web(String uri) {
//		super();
//		this.uri = uri;
//	}

	public fragment_web() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments()!=null){
			uri=getArguments().getString("url1");
			Log.i("info", uri);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_web, null);
			getActivity().setTheme(R.style.BrowserThemeDefault);
		tv = (TextView) view.findViewById(R.id.tvId);
		tv.setVisibility(View.GONE);
		webView = (WebView) view.findViewById(R.id.webView1);
		webView.setVisibility(View.VISIBLE);
		view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub

				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				// if (dialog == null) {
				// dialog = DialogUtil_KXT.createLoadingDialog(getActivity(),
				// "加载中..");
				// dialog.show();
				// }
			}

			@Override
			public void onPageFinished(WebView v, String url) {
				// TODO Auto-generated method stub
				// if (dialog != null && dialog.isShowing()) {
				// dialog.dismiss();
				// dialog = null;
				// }
				super.onPageFinished(v, url);
//				view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView v, int errorCode,
					String description, String failingUrl) {
				tv.setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
//				view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);// 是否支持JavaScript
		webView.getSettings().setBuiltInZoomControls(true);//
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setLoadsImagesAutomatically(true);// 是否加载图片
			webView.loadUrl(uri);
//		view.findViewById(R.id.img).startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.loading_animation));
		return view;
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if("http://v8api.kxt.com/alerts".equals(uri)){
			if (isVisibleToUser) {
	            //fragment可见时执行加载数据或者进度条等
				webView.loadUrl(uri);
	        } else {
	            //不可见时不执行操作
	        }
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
