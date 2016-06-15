package com.jyh.hjtzdxt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jyh.hjtzdxt.bean.Constant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
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

public class TJActivity extends Activity implements OnClickListener {
	private LinearLayout tj_ac_qq, tj_ac_xl, tj_ac_wx, tj_ac_py, tj_zt_color;
	private UMSocialService mController = UMServiceFactory
			.getUMSocialService(Constants.DESCRIPTOR);

	private String shareurl = Constant.newBaseUrl+"/app/share.html";
	private String sharetitle = "推荐应用【大赢家财经】给您";
	private String sharecontent = "黄金投资领导品牌";
	private LinearLayout image_back;
	private SharedPreferences preferences;
	private UMImage urlImage;
	// 手指上下滑动时的最小速度
	private static final int YSPEED_MIN = 1000;

	// 手指向右滑动时的最小距离
	private static final int XDISTANCE_MIN = 50;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		preferences = getSharedPreferences("setup", Context.MODE_PRIVATE);
			this.setTheme(R.style.BrowserThemeDefault);

//		// 透明状态栏
//		getWindow()
//				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		// 透明导航栏
//		getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		setContentView(R.layout.activity_tj);
		InitFind();
		configPlatforms();
	}

	private void InitFind() {
		tj_zt_color = (LinearLayout) findViewById(R.id.tj_zt_color);
			tj_zt_color.setBackgroundColor(Color.parseColor("#116bcc"));
		tj_ac_py = (LinearLayout) findViewById(R.id.tj_ac_py);
		tj_ac_xl = (LinearLayout) findViewById(R.id.tj_ac_xl);
		tj_ac_qq = (LinearLayout) findViewById(R.id.tj_ac_qq);
		tj_ac_wx = (LinearLayout) findViewById(R.id.tj_ac_wx);
		image_back = (LinearLayout) findViewById(R.id.sc_img_activity);
		InitClick();
	}

	private void InitClick() {
		tj_ac_py.setOnClickListener(this);
		tj_ac_xl.setOnClickListener(this);
		tj_ac_qq.setOnClickListener(this);
		tj_ac_wx.setOnClickListener(this);
		image_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tj_ac_qq:
			urlImage = new UMImage(TJActivity.this, R.drawable.shareimg);
			QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(sharecontent);
			qqShareContent.setTitle(sharetitle);
			qqShareContent.setShareImage(urlImage);
			qqShareContent.setTargetUrl(shareurl);
			mController.setShareMedia(qqShareContent);
			performShare(SHARE_MEDIA.QQ);
			mController.getConfig().closeToast();
			break;
		case R.id.tj_ac_xl:
			mController.getConfig().setSsoHandler(new SinaSsoHandler());
			mController.setShareContent(sharetitle+ shareurl);
			mController.setShareImage(urlImage);
			SmsHandler smsHandler = new SmsHandler();
			smsHandler.addToSocialSDK();
			performShare(SHARE_MEDIA.SINA);
			mController.getConfig().closeToast();
			break;
		case R.id.tj_ac_wx:
			urlImage = new UMImage(TJActivity.this, R.drawable.shareimg);
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			weixinContent.setShareContent(sharecontent);
			weixinContent.setTitle(sharetitle);
			weixinContent.setTargetUrl(shareurl);
			weixinContent.setShareMedia(urlImage);
			mController.setShareMedia(weixinContent);
			performShare(SHARE_MEDIA.WEIXIN);
			mController.getConfig().closeToast();
			break;
		case R.id.tj_ac_py:
			urlImage = new UMImage(TJActivity.this, R.drawable.shareimg);
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent(sharecontent);
			circleMedia.setShareImage(urlImage);
			circleMedia.setTitle(sharetitle);
			circleMedia.setTargetUrl(shareurl);
			mController.setShareMedia(circleMedia);
			performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			mController.getConfig().closeToast();
			break;
		case R.id.sc_img_activity:// 返回
			finish();
			break;
		default:
			break;
		}
	}

	private void performShare(SHARE_MEDIA platform) {
		mController.postShare(this, platform, new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				String showText = platform.toString();
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					showText += "平台分享成功";
				} else {
					showText += "平台分享失败";
				}
				Toast.makeText(TJActivity.this, showText, Toast.LENGTH_SHORT)
						.show();
			}
		});
		mController.getConfig().closeToast();
	}

	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// 添加QQ、QZone平台
		addQQQZonePlatform();

		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	private void addQQQZonePlatform() {
		String appId = Constant.APPID_QQ;
		String appKey = Constant.APPKEY_QQ;
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = Constant.APPID_WX;
		String appSecret = Constant.APPSECRET_WX;
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 根据requestCode获取对应的SsoHandler
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
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
			if (distanceX > XDISTANCE_MIN
					&& (distanceY < YDISTANCE_MIN && distanceY > -YDISTANCE_MIN)
					&& ySpeed < YSPEED_MIN) {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
			break;
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}
}
