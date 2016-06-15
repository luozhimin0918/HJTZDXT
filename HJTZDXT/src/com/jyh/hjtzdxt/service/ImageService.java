package com.jyh.hjtzdxt.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jyh.hjtzdxt.bean.ChatEmojiTitle;
import com.jyh.hjtzdxt.bean.ChatEmoji_New;
import com.jyh.hjtzdxt.bean.Constant;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;
import com.jyh.hjtzdxt.tool.FaceConversionUtil;

public class ImageService extends Service {

	private ExecutorService executorService;

	private RequestQueue queue;
	private Map<String, String> emojiMaps;

	private List<ChatEmojiTitle> emojiTitles;
	private List<ChatEmoji_New> emojis;
	private List<String> caitiaos;

	private boolean isLoaded = false;
	private boolean isLoaded2 = false;

	private int typeNum = 0;// 图片种类数

	private int size = 0;// 图片总个数
	private int _size = 0;// 已加载图片总个数

	public Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 111: {
				((KXTApplication) getApplication()).setChatEmoji_News(emojis);
				if (isLoaded) {
					if (!isLoaded2) {
						try {
							for (int i = 0; i < typeNum; i++) {
								FaceConversionUtil.getInstace().emoji.get(i).clear();
								FaceConversionUtil.getInstace().emojiList.get(i).clear();
								FaceConversionUtil.getInstace().getFileText(getApplicationContext(), i);
							}
							KXTApplication.isLoadedImg = true;
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							KXTApplication.isLoadedImg = false;
							KXTApplication.isLoadedImgError = true;
						}
						if (!KXTApplication.isLoadedImgError)
							sendBroadcast(new Intent("loadimg"));
					}
				} else {
					try {
						Thread.sleep(200);
						handler.sendEmptyMessage(111);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			case 100: {
				Toast.makeText(getApplicationContext(), "表情加载异常，正在再加载中，请稍后再试", 0).show();
				break;
			}
			default:
				break;
			}
			return false;
		}
	});

	private SQLiteDatabase db;

	private SCDataSqlte dataSqlte;

	private Mybind mybind;

	private LoadImgErrorreceiver imgReceiver;

	public ImageService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		imgReceiver = new LoadImgErrorreceiver();
		registerReceiver(imgReceiver, new IntentFilter("loaderror"));
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mybind;
	}

	public class Mybind extends Binder {
		public ImageService Getservire() {
			return ImageService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		executorService = Executors.newFixedThreadPool(10);
		emojiMaps = new HashMap<String, String>();
		emojiTitles = new ArrayList<ChatEmojiTitle>();
		caitiaos = new ArrayList<String>();
		emojis = new ArrayList<ChatEmoji_New>();

		dataSqlte = new SCDataSqlte(this);
		db = dataSqlte.getWritableDatabase();
		// getJsonObj(Constant.newBaseUrl + "/Appapi/Phiz/nav", 1);
		getJsonObj(Constant.newBaseUrl + "/Appapi/Phiz/all", 3);
		return super.onStartCommand(intent, flags, startId);
	}

	private void getJsonObj(String url, final int i) {
		queue = ((KXTApplication) getApplication()).getQueue();
		if (queue == null)
			queue = Volley.newRequestQueue(getApplicationContext());

		JsonObjectRequest request = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				try {
					switch (i) {
					case 1:
						if ("200".equals(arg0.getString("code"))) {
							org.json.JSONArray array = arg0.getJSONArray("data");
							for (int i = 0; i < array.length(); i++) {
								typeNum += 1;
								JSONObject object = array.getJSONObject(i);
								String code = object.getString("code");
								String name = object.getString("name");
								emojiTitles.add(new ChatEmojiTitle(typeNum, code, name, false));
								getEmoji(1, code, name);
							}
						}
						getJsonObj(Constant.newBaseUrl + "/Appapi/Caitiao/nav", 2);
						break;
					case 2:
						if ("200".equals(arg0.getString("code"))) {
							org.json.JSONArray array = arg0.getJSONArray("data");
							for (int i = 0; i < array.length(); i++) {
								typeNum += 1;
								JSONObject object = (JSONObject) array.get(i);
								String code = object.getString("code");
								String name = object.getString("name");
								emojiTitles.add(new ChatEmojiTitle(typeNum, code, name, true));
								getEmoji(2, code, name);
							}
							KXTApplication.emojiNum = typeNum;
							isLoaded = true;
							List<ChatEmojiTitle> list = emojiTitles;
							for (ChatEmojiTitle chatEmojiTitle : emojiTitles) {
								if (chatEmojiTitle.isCaitiao()) {
									list.remove(chatEmojiTitle);
									chatEmojiTitle.setName("彩条");
									list.add(0, chatEmojiTitle);
								}
							}
							((KXTApplication) getApplication()).setChatEmojiTitles(list);
							getApplication().getSharedPreferences("appinfo", getApplication().MODE_PRIVATE).edit()
									.putString("emojiNum", "" + typeNum).commit();
						}
						break;
					case 3:
						if ("200".equals(arg0.getString("code"))) {
							org.json.JSONArray array = arg0.getJSONArray("data");

							int len = array.length();
							for (int j = 0; j < len; j++) {
								JSONObject jsonObject = array.getJSONObject(j);
								String imagePath = jsonObject.getString("image");
								String code1 = jsonObject.getString("code");
								emojiMaps.put(code1, imagePath);
							}
						}
						getJsonObj(Constant.newBaseUrl + "/Appapi/Caitiao/all", 4);
						break;
					case 4:
						if ("200".equals(arg0.getString("code"))) {
							org.json.JSONArray array = arg0.getJSONArray("data");
							int len = array.length();
							for (int j = 0; j < len; j++) {
								JSONObject jsonObject = array.getJSONObject(j);
								String imagePath = jsonObject.getString("image");
								String code1 = jsonObject.getString("code");
								caitiaos.add(code1);
								Log.i("hehe", "caitiao=" + code1 + " " + imagePath);
								emojiMaps.put(code1, imagePath);
							}
						}
						((KXTApplication) getApplication()).setCaitiaos(caitiaos);
						((KXTApplication) getApplication()).setEmojiMaps(emojiMaps);
						getJsonObj(Constant.newBaseUrl + "/Appapi/Phiz/nav", 1);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub

			}
		});
		queue.add(request);
	}

	protected void getEmoji(final int i, final String code, String name) {
		// TODO Auto-generated method stub
		String url = null;
		if (i == 1)
			url = Constant.newBaseUrl + "/Appapi/Phiz/page?code=" + code;
		else
			url = Constant.newBaseUrl + "/Appapi/Caitiao/page?code=" + code;
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				try {
					if ("200".equals(arg0.getString("code"))) {
						org.json.JSONArray array = arg0.getJSONArray("data");
						int len = array.length();
						size += len;
						for (int j = 0; j < len; j++) {
							JSONObject jsonObject = array.getJSONObject(j);
							final String imagePath = jsonObject.getString("image");
							final String code1 = jsonObject.getString("code");
							// if (i == 2) {
							// // 彩条
							// caitiaos.add(code1);
							// }
							// emojiMaps.put(code1, imagePath);
							// saveImg2(code, imagePath, code1, i);
							saveImg(code, imagePath, code1);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
			}
		});
		queue.add(jsonObjectRequest);
	}

	private void saveImg(final String code, final String imagePath, final String code1) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// _size += 1;
				String result = "";
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					return;
				}
				String sdcard = Environment.getExternalStorageDirectory().toString();
				File file = new File(getFilesDir().getPath() + "/emoji/" + code);
				if (!file.exists()) {
					file.mkdirs();
				}
				int idx = imagePath.lastIndexOf(".");
				String ext = imagePath.substring(idx);
				file = new File(getFilesDir().getPath() + "/emoji/" + code + "/" + imagePath.substring(imagePath.lastIndexOf("/") + 1));
				try {
					if (file.exists()) {
						synchronized (ImageService.this) {
							_size += 1;
							if (size == _size) {
								handler.sendEmptyMessage(111);
							}
						}
						return;
					}
					InputStream inputStream = null;
					URL url = new URL(imagePath);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(20000);
					if (conn.getResponseCode() == 200) {
						inputStream = conn.getInputStream();
					}
					byte[] buffer = new byte[4096];
					int len = 0;
					FileOutputStream outStream = new FileOutputStream(file);
					while ((len = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					outStream.close();
					result = "图片已保存至：" + file.getAbsolutePath();
					// 通知图库更新
					// sendBroadcast(new
					// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					// Uri.fromFile(file)));
					ChatEmoji_New chatEmoji_New = new ChatEmoji_New(code1, imagePath, file.getPath(), code, false);
					if (!db.isOpen()) {
						db = dataSqlte.getWritableDatabase();
					}
					db.execSQL("insert into emoji (name,type,image,path) values (?,?,?,?);", new Object[] { chatEmoji_New.getName(),
							chatEmoji_New.getType(), chatEmoji_New.getImage(), chatEmoji_New.getPath() });
					// SQLiteDatabase.execSQL("DELETE FROM CUSTOMERS")
					synchronized (ImageService.this) {
						_size += 1;
						if (size == _size) {
							handler.sendEmptyMessage(111);
						}
					}
				} catch (Exception e) {
					result = "保存失败！" + e.getLocalizedMessage();
					synchronized (ImageService.this) {
						_size += 1;
					}
					file.delete();
				}
			}
		});
	}

	private void saveImg2(final String code, final String imagePath, final String code1, final int i) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// _size += 1;
				String result = "";
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					return;
				}
				String sdcard = Environment.getExternalStorageDirectory().toString();
				File file = new File(getFilesDir().getPath() + "/emoji/" + code);
				if (!file.exists()) {
					file.mkdirs();
				}
				int idx = imagePath.lastIndexOf(".");
				String ext = imagePath.substring(idx);
				file = new File(getFilesDir().getPath() + "/emoji/" + code + "/" + imagePath.substring(imagePath.lastIndexOf("/") + 1));
				boolean isct = false;
				if (i == 2) {
					isct = true;
				}
				ChatEmoji_New chatEmoji_New = new ChatEmoji_New(code1, imagePath, file.getPath(), code, isct);
				emojis.add(chatEmoji_New);
				try {
					if (file.exists()) {
						synchronized (ImageService.this) {
							_size += 1;
							if (size == _size) {
								handler.sendEmptyMessage(111);
							}
						}
						return;
					}
					InputStream inputStream = null;
					URL url = new URL(imagePath);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(20000);
					if (conn.getResponseCode() == 200) {
						inputStream = conn.getInputStream();
					}
					byte[] buffer = new byte[4096];
					int len = 0;
					FileOutputStream outStream = new FileOutputStream(file);
					while ((len = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					outStream.close();
					result = "图片已保存至：" + file.getAbsolutePath();
					synchronized (ImageService.this) {
						_size += 1;
						if (size == _size) {
							handler.sendEmptyMessage(111);
						}
					}
				} catch (Exception e) {
					result = "保存失败！" + e.getLocalizedMessage();
					synchronized (ImageService.this) {
						_size += 1;
					}
					file.delete();
				}
			}
		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(imgReceiver);
		super.onDestroy();
	}

	class LoadImgErrorreceiver extends BroadcastReceiver {

		public LoadImgErrorreceiver() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(100);
		}

	}
}
