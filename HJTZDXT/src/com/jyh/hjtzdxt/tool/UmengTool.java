package com.jyh.hjtzdxt.tool;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.jyh.hjtzdxt.R;
import com.jyh.hjtzdxt.bean.Constant;
import com.umeng.socialize.bean.SHARE_MEDIA;
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

public class UmengTool {
	/**
	 * 添加分享平台
	 * 
	 * @param activity
	 *            展示界面
	 */
	public static void configPlatforms(Activity activity) {
		addQQQZonePlatform(activity);
		addWXPlatform(activity);
	}

	/**
	 * 添加QQ分享平台
	 * 
	 * @param activity
	 *            展示界面
	 */
	public static void addQQQZonePlatform(Activity activity) {
		// String appId = "1101487761";
		// String appKey = "YJCbjYB5ql2LNRyQ";
		String appId = Constant.APPID_QQ;
		String appKey = Constant.APPKEY_QQ;
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId,
				appKey);
		qqSsoHandler.setTargetUrl("http://m.kuaixun360.com/");
		qqSsoHandler.addToSocialSDK();
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * 添加微信分享平台
	 * 
	 * @param activity
	 *            展示界面
	 */
	public static void addWXPlatform(Activity activity) {
		String appId = Constant.APPID_WX;
		String appSecret = Constant.APPSECRET_WX;
		UMWXHandler wxHandler = new UMWXHandler(activity, appId, appSecret);
		wxHandler.addToSocialSDK();
		UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId,
				appSecret);
		wxCircleHandler.showCompressToast(false);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * 设置分享内容
	 * 
	 * @param mController
	 *            友盟设置
	 * @param activity
	 *            展示界面
	 * @param title
	 *            标题
	 * @param weburl
	 *            分享网址
	 * @param discription
	 *            描述
	 * @param thumb
	 *            分享图片
	 */
	public static void setShareContent(UMSocialService mController,
			Activity activity, String title, String weburl, String discription,
			String thumb) {
		Log.i("info", title + " " + weburl + " " + discription);
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity,
				Constant.APPID_QQ, Constant.APPKEY_QQ);
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(discription);
		UMImage urlImage;
		if (null != thumb && !"".equals(thumb)) {
			urlImage = new UMImage(activity, thumb);
		} else {
			urlImage = new UMImage(activity, R.drawable.ic_launcher);
		}
		// 微信
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent("【" + title + "】" + discription + weburl);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl(weburl);
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);
		// 朋友圈
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent("【" + title + "】" + discription + weburl);
		circleMedia.setTitle(title);
		circleMedia.setShareImage(urlImage);
		circleMedia.setTargetUrl(weburl);
		mController.setShareMedia(circleMedia);
		// qzone
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent("【" + title + "】" + discription + weburl);
		qzone.setTargetUrl(weburl);
		qzone.setTitle(title);
		qzone.setShareImage(urlImage);
		mController.setShareMedia(qzone);
		// qq
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent
				.setShareContent("【" + title + "】" + discription + weburl);
		qqShareContent.setTitle(title);
		qqShareContent.setShareImage(urlImage);
		qqShareContent.setTargetUrl(weburl);
		mController.setShareMedia(qqShareContent);

		// 添加新浪sso授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.setShareContent("【" + title + "】" + discription + weburl);
		mController.setShareImage(urlImage);
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		mController.getConfig().closeToast();
	}

	/**
	 * 设置所有平台，展示出来
	 * 
	 * @param activity
	 *            展示界面
	 * @param mController
	 *            友盟配置
	 */
	public static void addCustomPlatforms(Activity activity,
			UMSocialService mController) {
		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.SINA);
		mController.openShare(activity, false);
	}

	/**
	 * 
	 * @param mController
	 *            友盟分享平台设置
	 * @param requestCode
	 *            请求码
	 * @param resultCode
	 *            返回码
	 * @param data
	 *            intent值
	 */
	public static void setSSORollBack(UMSocialService mController,
			int requestCode, int resultCode, Intent data) {
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

	}
}
