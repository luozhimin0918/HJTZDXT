package com.jyh.hjtzdxt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.hjtzdxt.bean.ChatMsgEntity;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.bean.RoomRole;
import com.jyh.hjtzdxt.customtool.BounceTopEnter;
import com.jyh.hjtzdxt.customtool.MaterialDialog;
import com.jyh.hjtzdxt.customtool.OnBtnClickL;
import com.jyh.hjtzdxt.customtool.SlideBottomExit;
import com.jyh.hjtzdxt.socket.NetworkCenter;
import com.jyh.hjtzdxt.socket.VersionManager;
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;
import com.jyh.hjtzdxt.tool.ImageDownLoader;
import com.jyh.hjtzdxt.tool.NormalPostRequest;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity {

	private KXTApplication application;
	LinearLayout rl_splash;
	public static boolean isLoadingInit = false;
	private VersionManager versionManager;
	private SharedPreferences preferences;
	private boolean isEnter = false;
	private static MaterialDialog testDialog;// 网络异常提示Dialog
	private BounceTopEnter bas_in;
	private SlideBottomExit bas_out;
	private RequestQueue queue;
	private boolean IsFirstError = true;
	private SCDataSqlte sqlOpenHelper;// 用以保存直播室相关信息
	protected boolean isLoadAD;// 判断是否有广告
	protected SharedPreferences preference_userinfo;
	protected String require_login;// 强制登录
	protected boolean isNeedLogin = false;// 是否强制登录
	public String load_ad_image;// 广告图片下载地址
	private String adurl2;// 广告图片点击跳转地址
	private boolean isFirstLoading = true;// 用以防止数据库重复读写

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter);
		MobclickAgent.setDebugMode(true);

		rl_splash = (LinearLayout) findViewById(R.id.rl_splash);
		bas_in = new BounceTopEnter();
		bas_out = new SlideBottomExit();
		application = (KXTApplication) getApplication();
		application.addAct(this);
		if (null == application.getQueue()) {
			queue = Volley.newRequestQueue(this);
			application.setQueue(queue);
		} else {
			queue = application.getQueue();
		}

		handler.sendEmptyMessageDelayed(40, 10 * 1000);
		versionManager = VersionManager.getInstance();
		testDialog = new MaterialDialog(this);

		if (NetworkCenter.checkNetwork_JYH(this)) {
			versionManager.checkVersion(this, handler);
		} else {
			handler.sendEmptyMessageDelayed(40, 8 * 1000);
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 30:
				Toast.makeText(WelcomeActivity.this, "获取版本信息异常", Toast.LENGTH_SHORT).show();
				TagsTask(Constant.newBaseUrl + "/Appapi/index/Config?system=android", 5);
				break;
			case 40:
				if (!isEnter) {
					if (!testDialog.isShowing()) {
						testDialog//
								.btnNum(1).content("网络异常，请检查网络。")//
								.btnText("确定")//
								.showAnim(bas_in)//
								.dismissAnim(bas_out)//
								.show();
						testDialog.setOnBtnClickL(new OnBtnClickL() {

							@Override
							public void onBtnClick() {
								// TODO Auto-generated method stub
								testDialog.dismiss();
								handler.sendEmptyMessageDelayed(90, 1000);
							}
						});
						testDialog.setCanceledOnTouchOutside(false);
					}
				}
				break;
			case 50:
				if (!testDialog.isShowing()) {
					testDialog.content("当前网络不稳定，请检查手机网络")//
							.btnText("取消", "确定")//
							.showAnim(bas_in)//
							.dismissAnim(bas_out)//
							.show();
					testDialog.setOnBtnClickL(new OnBtnClickL() {// left btn
								@Override
								public void onBtnClick() {
									testDialog.dismiss();
									handler.sendEmptyMessageDelayed(90, 1000);
								}
							}, new OnBtnClickL() {// right btn click listener
								@Override
								public void onBtnClick() {
									Intent intent = null;
									// 先判断当前系统版本
									if (android.os.Build.VERSION.SDK_INT > 10) { // 3.0以上
										intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
									} else {
										intent = new Intent();
										intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
									}
									startActivity(intent);
									testDialog.dismiss();
									handler.sendEmptyMessageDelayed(90, 1000);

								}
							});
					testDialog.setCanceledOnTouchOutside(false);
				}
				break;
			case 60:
				// 启动更新
				preferences = getSharedPreferences("versions", Context.MODE_PRIVATE);
				String description = preferences.getString("description", "快讯通VIP直播室有新版啦");
				final String versionurl = preferences.getString("versionurl", "http://kxt.com/down.html");
				if (!testDialog.isShowing()) {
					testDialog.content(description)//
							.btnText("取消", "确定")//
							.showAnim(bas_in)//
							.dismissAnim(bas_out)//
							.show();
					testDialog.setOnBtnClickL(new OnBtnClickL() {// left btn
								@Override
								public void onBtnClick() {
									handler.sendEmptyMessageDelayed(70, 2 * 1000);
									testDialog.dismiss();
								}
							}, new OnBtnClickL() {// right btn click listener
								@Override
								public void onBtnClick() {
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_VIEW);
									intent.setData(Uri.parse(versionurl));
									startActivity(intent);
									testDialog.dismiss();
									handler.sendEmptyMessageDelayed(90, 1000);
								}
							});
					testDialog.setCanceledOnTouchOutside(false);
				}
				break;
			case 70:
				TagsTask(Constant.newBaseUrl + "/Appapi/index/Config?system=android", 5);
				break;
			case 80:
				if (null != testDialog && testDialog.isShowing()) {
					testDialog.dismiss();
				}
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case 90:
				application.exitAppAll();
			default:
				break;
			}
		};
	};

	private void TagsTask(final String url, final int i) {
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (testDialog.isShowing()) {
					return;
				}
				ResolveData(response, i);
				IsFirstError = true;
				// getLiveInfo(Constant.baseUrl
				// + "/index/Config?system=android");
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				if (IsFirstError) {
					TagsTask(url, i);
					IsFirstError = false;
				} else {
					isEnter = false;
					handler.sendEmptyMessage(40);
				}
			}

		});
		queue.add(jsObjRequest);
	}

	public void ResolveData(JSONObject jsondata, int i) {
		try {
			JSONArray array;
			switch (i) {
			case 5: {
				// App配置信息
				JSONObject job = jsondata;
				JSONObject data = job.getJSONObject("data");
				// appinfo 配置信息
				JSONObject appinfoJob = data.getJSONObject("appinfo");
				require_login = appinfoJob.getString("require_login");
				// loadAd 广告
				JSONObject loadAd = data.getJSONObject("load_ad");

				adurl2 = loadAd.getString("url");
				load_ad_image = loadAd.getString("image");

				if (load_ad_image != null && !"".equals(load_ad_image))
					isLoadAD = true;
				else
					isLoadAD = false;

				// userinfo 用户信息
				JSONObject userinfoJob = data.getJSONObject("userinfo");
				// videoinfo 直播室信息
				JSONObject videoinfoJob = data.getJSONObject("video_info");
				JSONObject detailJob = null;
				JSONObject qinjia = null;
				if (videoinfoJob.getString("type").equals("gensee")) {
					detailJob = videoinfoJob.getJSONObject("gensee");
				} else if (videoinfoJob.getString("type").equals("live_108")) {
					qinjia = videoinfoJob.getJSONObject("live_108");
				}
				// roomrole 用户角色信息
				JSONArray roomrolesJoA = data.getJSONArray("roomrole");
				List<RoomRole> roomRoles = new ArrayList<RoomRole>();

				JSONObject job2;

				for (int i1 = 0; i1 < roomrolesJoA.length(); i1++) {
					job2 = roomrolesJoA.getJSONObject(i1);
					RoomRole roleRoomRole = new RoomRole();
					roleRoomRole.setId(job2.getString("id"));
					roleRoomRole.setName(job2.getString("name"));
					roleRoomRole.setType(job2.getString("type"));
					roleRoomRole.setLimit_chat_time(job2.getString("limit_chat_time"));
					roleRoomRole.setPower_whisper(job2.getString("power_whisper"));
					roleRoomRole.setLimit_colorbar_time(job2.getString("limit_colorbar_time"));
					roleRoomRole.setPower_upload_pic(job2.getString("power_upload_pic"));
					roleRoomRole.setLimit_account_time(job2.getString("limit_account_time"));
					roleRoomRole.setStatus(job2.getString("status"));
					roleRoomRole.setSort(job2.getString("sort"));
					roleRoomRole.setPower_visit_room(job2.getString("power_visit_room"));
					roleRoomRole.setStyle_chat_text(job2.getString("style_chat_text"));
					roleRoomRole.setImage(job2.getString("image"));

					roomRoles.add(roleRoomRole);
					new ImageDownLoader(WelcomeActivity.this).loadImage(job2.getString("image"), null);
				}

				preferences = getSharedPreferences("appinfo", Context.MODE_PRIVATE);
				Editor editor1 = preferences.edit();

				// 强制登录
				editor1.putString("require_login", require_login);
				// 不用强制登录
				// editor.putString("require_login", "");

				editor1.putString("appid", appinfoJob.getString("appid"));
				editor1.putString("name", appinfoJob.getString("name"));
				editor1.putString("gate", appinfoJob.getString("gate"));
				editor1.putString("kefu_url", appinfoJob.getString("kefu_url"));
				editor1.putString("userlist_url", appinfoJob.getString("userlist_url"));
				editor1.putString("images_url", appinfoJob.getString("images_url"));
				editor1.putString("course_url", appinfoJob.getString("course_url"));
				editor1.putString("summary_url", appinfoJob.getString("summary_url"));
				editor1.putString("bulletin_url", appinfoJob.getString("bulletin_url"));
				editor1.putString("fn_nav_url", appinfoJob.getString("fn_nav_url"));
				editor1.putString("fn_url", appinfoJob.getString("fn_url"));
				editor1.putString("cjrl_url", appinfoJob.getString("cjrl_url"));
				editor1.putString("alters_url", appinfoJob.getString("alters_url"));
				editor1.putString("hq_url", appinfoJob.getString("hq_url"));
				editor1.putString("upload_images_url", appinfoJob.getString("upload_images_url"));
				editor1.putString("register_url", appinfoJob.getString("register_url"));

				preference_userinfo = getSharedPreferences("userinfo", Context.MODE_PRIVATE);

				final Editor userEdit = preference_userinfo.edit();
				userEdit.putString("name", userinfoJob.getString("name"));
				userEdit.putString("rid", userinfoJob.getString("rid"));
				userEdit.putString("uid", userinfoJob.getString("id"));
				userEdit.commit();

				editor1.putString("video-type1", videoinfoJob.getString("type"));
				if (videoinfoJob.getString("type").equals("live_108")) {
					if (qinjia != null || !"".equals(qinjia)) {
						// gensee
						editor1.putString("video-domain", " ");
						editor1.putString("video-id", " ");
						editor1.putString("video-type", " ");
						editor1.putString("video-password", " ");
						// Gotye
						editor1.putString("video-Gotyeroomid", qinjia.getString("ROOMID"));
						editor1.putString("video-Gotyepassword", qinjia.getString("PASSWORD"));
					}
				} else {
					if (detailJob != null || !"".equals(detailJob)) {
						// gensee
						editor1.putString("video-domain", detailJob.getString("domain"));
						editor1.putString("video-id", detailJob.getString("id"));
						editor1.putString("video-type", detailJob.getString("type"));
						editor1.putString("video-password", detailJob.getString("password"));
						// Gotye
						editor1.putString("video-Gotyeroomid", "");
						editor1.putString("video-Gotyepassword", "");
					}
				}
				editor1.commit();

				if (isFirstLoading) {
					// 防止多次存储数据
					sqlOpenHelper = new SCDataSqlte(WelcomeActivity.this);
					SQLiteDatabase dbw = sqlOpenHelper.getWritableDatabase();

					Map<String, String> map = new HashMap<String, String>();
					for (int i1 = 0; i1 < roomRoles.size(); i1++) {

						RoomRole roomRole = roomRoles.get(i1);
						if ("1".equals(roomRole.getId())) {
							userEdit.putString("r_name", roomRole.getName());
							userEdit.putString("limit_chat_time", roomRole.getLimit_chat_time());
							userEdit.putString("limit_colorbar_time", roomRole.getLimit_colorbar_time());
							userEdit.putString("image", roomRole.getImage());
							userEdit.putString("power_visit_room", roomRole.getPower_visit_room());
							userEdit.commit();
						}
						dbw.execSQL(
								"insert into roomrole (id,name,type, limit_chat_time, power_whisper,"
										+ "limit_colorbar_time,power_upload_pic,limit_account_time,"
										+ "status,sort,power_visit_room,style_chat_text,image) values (?,?,?,?,?,?,?,?,?,?,?,?,?);",
								new Object[] { roomRole.getId(), roomRole.getName(), roomRole.getType(), roomRole.getLimit_chat_time(),
										roomRole.getPower_whisper(), roomRole.getLimit_colorbar_time(), roomRole.getPower_upload_pic(),
										roomRole.getLimit_account_time(), roomRole.getStatus(), roomRole.getSort(),
										roomRole.getPower_visit_room(), roomRole.getStyle_chat_text(), roomRole.getImage() });
					}
					dbw.close();
					isFirstLoading = false;
				}

				String token = preference_userinfo.getString("token", null);

				Map<String, String> map = new HashMap<String, String>();
				map.put("token", token == null ? "" : token);

				// 获取用户登录信息
				NormalPostRequest normalPostRequest = new NormalPostRequest(Constant.newBaseUrl + "/Appapi/Member/userinfo",
						new Listener<JSONObject>() {

							private SQLiteDatabase dbw;

							@Override
							public void onResponse(JSONObject arg0) {
								// TODO Auto-generated method stub
								String code;
								try {
									code = arg0.getString("code");
									if ("200".equals(code)) {
										JSONObject data = arg0.getJSONObject("data");
										userEdit.putString("login_uid", data.getString("id"));
										userEdit.putString("login_rid", data.getString("rid"));
										userEdit.putString("login_name", data.getString("name"));

										dbw = sqlOpenHelper.getWritableDatabase();
										Cursor cursor = dbw.rawQuery("select * from roomrole where id=?",
												new String[] { data.getString("rid") });
										while (cursor.moveToNext()) {
											userEdit.putString("r_name", cursor.getString(cursor.getColumnIndex("name")));
											userEdit.putString("limit_chat_time",
													cursor.getString(cursor.getColumnIndex("limit_chat_time")));
											userEdit.putString("limit_colorbar_time",
													cursor.getString(cursor.getColumnIndex("limit_colorbar_time")));
											userEdit.putString("image", cursor.getString(cursor.getColumnIndex("image")));
											userEdit.putString("power_visit_room",
													cursor.getString(cursor.getColumnIndex("power_visit_room")));
										}
										cursor.close();
										dbw.close();
										userEdit.commit();
									} else {
										// 获取登录信息失败
										if (require_login != null && "1".equals(require_login)) {
											isNeedLogin = true;
										} else {
											isNeedLogin = false;
										}
									}
								} catch (JSONException e) {
									// 获取登录信息失败
									if (require_login != null && "1".equals(require_login)) {
										isNeedLogin = true;
									} else {
										isNeedLogin = false;
									}
									e.printStackTrace();
								}
								TagsTask(Constant.newBaseUrl + "/Appapi/Chat/history", 6);
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError arg0) {
								// 获取登录信息失败
								if (require_login != null && "1".equals(require_login)) {
									isNeedLogin = true;
								} else {
									isNeedLogin = false;
								}
								TagsTask(Constant.newBaseUrl + "/Appapi/Chat/history", 6);
							}
						}, map);
				queue.add(normalPostRequest);
				break;
			}
			case 6: {
				// 历史聊天记录
				Log.i("info", "history=" + jsondata);
				JSONArray array1 = jsondata.getJSONArray("data");
				List<ChatMsgEntity> chatMsgEntities = new ArrayList<ChatMsgEntity>();
				for (int i1 = 0; i1 < array1.length(); i1++) {
					JSONObject object1 = (JSONObject) array1.get(i1);
					Log.i("info1", object1.toString());
					ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
					if (object1.getString("f_name") != null && !object1.getString("f_name").equals("")) {
						chatMsgEntity.setData(object1.getString("data"));
						chatMsgEntity.setIs_checked(object1.getString("is_checked"));
						chatMsgEntity.setT_uid(object1.getString("t_uid"));
						chatMsgEntity.setT_rid(object1.getString("t_rid"));
						chatMsgEntity.setT_name(object1.getString("t_name"));
						chatMsgEntity.setF_name(object1.getString("f_name"));
						chatMsgEntity.setF_rid(object1.getString("f_rid"));
						chatMsgEntity.setF_uid(object1.getString("f_uid"));
						chatMsgEntity.setTime(object1.getString("time"));
						chatMsgEntity.setId(object1.getString("id"));
						chatMsgEntities.add(chatMsgEntity);

					}
				}
				application.setChatMsgEntities(chatMsgEntities);
				final Intent Mainintent = new Intent(WelcomeActivity.this, MainActivity.class);

				if (!isLoadAD) {
					// 不用加载广告
					if (isNeedLogin) {
						// 强制登录
						Intent LoginIntent = new Intent(WelcomeActivity.this, Login_One.class);
						startActivity(LoginIntent);
						finish();
					} else {
						// 不用强制登录
						startActivity(Mainintent);
						finish();
					}
				} else
					// 加载广告
					if(load_ad_image!=null&&!"".equals(load_ad_image)){
						Load_ad();
					}else{
						if (isNeedLogin) {
							// 强制登录
							Intent LoginIntent = new Intent(WelcomeActivity.this, Login_One.class);
							startActivity(LoginIntent);
							finish();
						} else {
							// 不用强制登录
							startActivity(Mainintent);
							finish();
						}
					}
				
//					new ImageDownLoader(application).loadImage(load_ad_image, new AsyncImageLoaderListener() {
//
//						@Override
//						public void onImageLoader(Bitmap bitmap) {
//							Log.i("info", "bitmap=null?" + (bitmap == null));
//							if (bitmap != null) {
//								Load_ad(bitmap);
//							} else {
//								if (isNeedLogin) {
//									// 强制登录
//									Intent LoginIntent = new Intent(WelcomeActivity.this, Login_One.class);
//									startActivity(LoginIntent);
//									finish();
//								} else {
//									// 不用强制登录
//									startActivity(Mainintent);
//									finish();
//								}
//							}
//						}
//					});
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("Response", "异常");
		}
	}

	/*
	 * 广告加载
	 */
	private void Load_ad() {
		Intent intent = new Intent(WelcomeActivity.this, AdActivity.class);
		intent.putExtra("image", load_ad_image);
		intent.putExtra("url", adurl2);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in,R.anim.fade_out); 
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		testDialog = null;
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		isEnter = true;
		MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		// onPageEnd 在onPause
		// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}
