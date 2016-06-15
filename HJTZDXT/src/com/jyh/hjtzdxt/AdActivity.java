package com.jyh.hjtzdxt;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.customtool.DownLoaderImage;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.umeng.soexample.commons.Constants;

/**
 * 广告界面
 * 
 * @author beginner
 * @date 创建时间：2015年7月21日 下午4:53:38
 * @version 1.0
 */
public class AdActivity extends FragmentActivity implements OnClickListener {

	private SimpleDraweeView img;
	private ImageView img2;
	// private SharedPreferences preference;
	private String url;
	private Intent intent;
	private Timer timer;
	private String imgpath;
	private SharedPreferences preferences;
	private ViewGroup ad_zt_color;
	private WebView webView;

	private UMSocialService mController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);
	private String title;
	private String share;
	private UMImage urlImage;
	private String require_login;
	private boolean isNeedLogin;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				findViewById(R.id.imgId).setVisibility(View.VISIBLE);
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						startA_ctivity();
					}
				}, 2 * 1000);
				break;
			case 2:
				startA_ctivity();
				break;
			default:
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = this.getSharedPreferences("setup", Context.MODE_PRIVATE);
		// this.setTheme(R.style.BrowserThemeDefault);
		this.setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		// 透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// 透明导航栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ad);
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new WebViewClient() {

		});
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedTitle(WebView view, String titl) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(view, titl);
				((TextView) findViewById(R.id.ad_title_tv)).setText(titl);
				title = titl;
				configPlatforms();
				setShareContent();
			}

		});
		// ad_zt_color = (ViewGroup) findViewById(R.id.ad_zt_color);
		// ad_zt_color.setBackgroundColor(Color.parseColor("#116bcc"));

		require_login = getSharedPreferences("appinfo", MODE_PRIVATE).getString("require_login", "");
		preferences = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		String token = preferences.getString("token", null);
		long expired_time = preferences.getLong("expired_time", 0);
		if (token != null && expired_time > (System.currentTimeMillis() / 1000)) {
			// 登录有效
			isNeedLogin = false;
		} else {
			if (require_login != null && "1".equals(require_login)) {
				isNeedLogin = true;
			} else {
				isNeedLogin = false;
			}
		}

		imgpath = getIntent().getStringExtra("image");
		url = getIntent().getStringExtra("url");
		Log.i("url", url + " " + imgpath);
		img = (SimpleDraweeView) findViewById(R.id.img);
		img2 = (ImageView) findViewById(R.id.img2);
		img.setOnClickListener(this);
		img2.setOnClickListener(this);
		findViewById(R.id.ad_ima_share).setOnClickListener(this);
		findViewById(R.id.ad_img_back).setOnClickListener(this);
		intent = new Intent(AdActivity.this, MainActivity.class);
		Bitmap bitmap = new DownLoaderImage(AdActivity.this).getBitmapCache(imgpath);
		Log.i("info", "_bitmap=null?" + (bitmap == null));
		try {
			ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
				@Override
				public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
					handler.sendEmptyMessage(1);
				}

				@Override
				public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
				}

				@Override
				public void onFailure(String id, Throwable throwable) {
					Log.i("hehe", "Fresco error:" + throwable);
					handler.sendEmptyMessage(2);
				}
			};
			DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener)
					.setUri(Uri.parse(imgpath))
					// other setters
					.build();
			img.setController(controller);
			timer = new Timer();
			share = url;
			urlImage = new UMImage(getApplicationContext(), bitmap);
		} catch (Exception exception) {
			exception.printStackTrace();
			startA_ctivity();
		}
	}

	private void configPlatforms() {
		addQQQZonePlatform();
		addWXPlatform();
	}

	private void addQQQZonePlatform() {
		String appId = Constant.APPID_QQ;
		String appKey = Constant.APPKEY_QQ;
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appId, appKey);
		qqSsoHandler.setTargetUrl("http://m.kuaixun360.com/");
		qqSsoHandler.addToSocialSDK();
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(AdActivity.this, appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	private void addWXPlatform() {
		String appId = Constant.APPID_WX;
		String appSecret = Constant.APPSECRET_WX;
		UMWXHandler wxHandler = new UMWXHandler(AdActivity.this, appId, appSecret);
		wxHandler.addToSocialSDK();
		UMWXHandler wxCircleHandler = new UMWXHandler(AdActivity.this, appId, appSecret);
		wxCircleHandler.showCompressToast(false);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	private void setShareContent() {

		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(AdActivity.this, Constant.APPID_QQ, Constant.APPKEY_QQ);
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(title);

		// 微信
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(title);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl(share);
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);
		// 朋友圈
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(title);
		circleMedia.setTitle(title);
		circleMedia.setShareImage(urlImage);
		circleMedia.setTargetUrl(share);
		mController.setShareMedia(circleMedia);
		// qzone
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(title);
		qzone.setTargetUrl(share);
		qzone.setTitle(title);
		qzone.setShareImage(urlImage);
		mController.setShareMedia(qzone);
		// qq
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(title);
		qqShareContent.setTitle(title);
		qqShareContent.setShareImage(urlImage);
		qqShareContent.setTargetUrl(share);
		mController.setShareMedia(qqShareContent);

		// 添加新浪sso授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.setShareContent("【" + title + "】" + share);
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		mController.getConfig().closeToast();
	}

	private void addCustomPlatforms() {

		// 添加QQ平台
		addQQQZonePlatform();

		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.SINA);
		mController.openShare(AdActivity.this, false);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.img:
			// 广告跳转
			if (url != null && !url.equals("")) {
				timer.cancel();
				timer.purge();
				webView.loadUrl(url);
				findViewById(R.id.imgId).setVisibility(View.GONE);
				findViewById(R.id.webviewId).setVisibility(View.VISIBLE);
			}
			break;
		case R.id.img2:
		case R.id.ad_img_back:
			timer.cancel();
			timer.purge();
			startA_ctivity();
			break;
		case R.id.ad_ima_share:
			addCustomPlatforms();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		timer.cancel();
		timer.purge();
		startA_ctivity();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 根据requestCode获取对应的SsoHandler
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}

	private void startA_ctivity() {
		if (isNeedLogin) {
			Intent LoginIntent = new Intent(AdActivity.this, Login_One.class);
			LoginIntent.putExtra("from", "welcome");
			startActivity(LoginIntent);
			finish();
		} else {
			startActivity(intent);
			finish();
		}
	}

}
