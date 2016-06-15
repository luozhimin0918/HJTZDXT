package com.jyh.hjtzdxt.tool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.jyh.hjtzdxt.bean.ChatEmoji_New;
import com.jyh.hjtzdxt.bean.KXTApplication;

/**
 * 
 ****************************************** 
 * @author 廖乃波
 * @文件名称 : FaceConversionUtil.java
 * @创建时间 : 2013-1-27 下午02:34:09
 * @文件描述 : 表情轉換工具
 ****************************************** 
 */
public class FaceConversionUtil implements ClearTool {
	private boolean IsCaiTiao = false;
	/** 每一页表情的个数 */
	private int pageSize = 20;// 普通表情每页个数
	private int pageSizeCT = 6;// 彩条每页个数
	private static FaceConversionUtil mFaceConversionUtil;

	/** 保存于内存中的表情HashMap */
	private HashMap<String, String> emojiMap = new HashMap<String, String>();
	/** 保存于内存中的表情集合 */
	public List<List<ChatEmoji_New>> emoji = new ArrayList<List<ChatEmoji_New>>();
	/** 表情分页的结果集合 */
	public List<List<List<ChatEmoji_New>>> emojiList = new ArrayList<List<List<ChatEmoji_New>>>();

	private int threadnum = -1;
	private List<ChatEmoji_New> chatEmoji_News;
	private List<List<ChatEmoji_New>> lists;

	private FaceConversionUtil() {
		emojiNum = KXTApplication.emojiNum;
		for (int j = 0; j < emojiNum; j++) {
			emoji.add(new ArrayList<ChatEmoji_New>());
			emojiList.add(new ArrayList<List<ChatEmoji_New>>());
		}
	}

	public HashMap<String, String> getEmojiMap() {
		return emojiMap;
	}

	public void setEmojiMap(HashMap<String, String> emojiMap) {
		this.emojiMap = emojiMap;
	}

	public static FaceConversionUtil getInstace() {
		if (mFaceConversionUtil == null) {
			mFaceConversionUtil = new FaceConversionUtil();
		}
		return mFaceConversionUtil;
	}

	/**
	 * 添加表情
	 * 
	 * @param context
	 * @param imgId
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, String imgpath, String path, String spannableString) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
		spannableString = "[" + spannableString + "]";
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		SpannableString spannable = null;
		if (bitmap != null) {
			float density = DisplayUtilJYH.getDpi((Activity) context);
			bitmap = Bitmap.createScaledBitmap(bitmap, (int) (density * 15), (int) (density * 15), true);
			ImageSpan imageSpan = new ImageSpan(context, bitmap);
			spannable = new SpannableString(spannableString);
			spannable.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			spannable = new SpannableString(spannableString);
		}
		return spannable;
	}

	private int emojiNum;

	/**
	 * 获取资源文件中的数据
	 * 
	 * @param context
	 */
	public void getFileText(Context context, int i) {

		threadnum = i;
		chatEmoji_News = emoji.get(threadnum);
		lists = emojiList.get(threadnum);
		ParseData(FileUtils.getEmojiFile(context, i), context, i);
	}

	/**
	 * 解析字符
	 * 
	 * @param data
	 * @param i2
	 */
	private void ParseData(List<ChatEmoji_New> data, Context context, int i2) {
		if (data == null) {
			return;
		}
		ChatEmoji_New emojEentry = new ChatEmoji_New();
		if (threadnum == -1) {
			Toast.makeText(context, "内部异常", Toast.LENGTH_SHORT).show();
		} else {
			try {
				IsCaiTiao = data.get(0).isCaitiao();
				for (ChatEmoji_New str : data) {
					chatEmoji_News.add(str);
				}

				int pageCount;
				if (IsCaiTiao) {
					pageCount = (int) Math.ceil(chatEmoji_News.size() / pageSizeCT + 0.1);
				} else {
					pageCount = (int) Math.ceil(chatEmoji_News.size() / pageSize + 0.1);
				}
				for (int i = 0; i < pageCount; i++) {
					lists.add(getData(i, threadnum));//
				}
			} catch (Exception e) {
				e.printStackTrace();
				KXTApplication.isLoadedImgError = true;
				Toast.makeText(context, "数据解析异常", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 获取分页数据
	 * 
	 * @param page
	 * @return
	 */
	private List<ChatEmoji_New> getData(int page, int threadnum) {
		List<ChatEmoji_New> list = new ArrayList<ChatEmoji_New>();

		int _pageSize = 0;
		if (IsCaiTiao)
			_pageSize = pageSizeCT;
		else
			_pageSize = pageSize;

		int startIndex = page * _pageSize;
		int endIndex = startIndex + _pageSize;
		if (endIndex > chatEmoji_News.size()) {
			endIndex = chatEmoji_News.size();
		}
		Log.i("info", startIndex + " " + endIndex);
		// 不这么写，会在viewpager加载中报集合操作异常，我也不知道为什么
		list.addAll(chatEmoji_News.subList(startIndex, endIndex));

		if (!IsCaiTiao) {
			if (list.size() < _pageSize) {
				for (int i = list.size(); i < _pageSize; i++) {
					ChatEmoji_New object = new ChatEmoji_New();
					list.add(object);
				}
			}
			if (list.size() == _pageSize) {
				ChatEmoji_New object = new ChatEmoji_New();
				object.setImage("R.drawable.face_del_icon");
				list.add(object);
			}
		}

		return list;
	}

	@Override
	public void ClearDate() {
		// TODO Auto-generated method stub
		for (int i = 0; i < emojiNum; i++) {
			emoji.get(i).clear();
			emojiList.get(i).clear();
		}
	}

}