package com.jyh.hjtzdxt.tool;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.jyh.hjtzdxt.bean.ChatEmojiTitle;
import com.jyh.hjtzdxt.bean.ChatEmoji_New;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;

/**
 * 
 ******************************************
 * @author 廖乃波
 * @文件名称 : FileUtils.java
 * @创建时间 : 2013-1-27 下午02:35:09
 * @文件描述 : 文件工具类
 ******************************************
 */
public class FileUtils {
	/**
	 * 读取表情配置文件
	 * 
	 * @param context
	 * @return
	 */
	public static List<ChatEmoji_New> getEmojiFile(Context context, int i) {
		try {
			List<ChatEmoji_New> list = new ArrayList<ChatEmoji_New>();
			list.clear();
			SQLiteDatabase database = new SCDataSqlte(context).getReadableDatabase();
			Cursor cursor = database.query("emoji", null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				List<ChatEmojiTitle> emojiTitles = ((KXTApplication) context.getApplicationContext()).getChatEmojiTitles();
				if (emojiTitles != null && emojiTitles.get(i) != null) {
					ChatEmojiTitle chatEmojiTitle = emojiTitles.get(i);
					boolean isCaitiao = false;
					if (chatEmojiTitle.isCaitiao()) {
						isCaitiao = true;
					}
					if (chatEmojiTitle.getCode().equals(cursor.getString(cursor.getColumnIndex("type"))))
						list.add(new ChatEmoji_New(cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor
								.getColumnIndex("image")), cursor.getString(cursor.getColumnIndex("path")), cursor.getString(cursor
								.getColumnIndex("type")), isCaitiao));
				}
			}
			cursor.close();
			database.close();
			// List<ChatEmoji_New> list2 = ((KXTApplication)
			// context.getApplicationContext()).getChatEmoji_News();
			// if (list2 != null)
			// for (ChatEmoji_New chatEmoji_New : list2) {
			// List<ChatEmojiTitle> emojiTitles = ((KXTApplication)
			// context.getApplicationContext()).getChatEmojiTitles();
			// if (emojiTitles != null && emojiTitles.get(i) != null) {
			// ChatEmojiTitle chatEmojiTitle = emojiTitles.get(i);
			// if (chatEmojiTitle.getCode().equals(chatEmoji_New.getType()))
			// list.add(chatEmoji_New);
			// }
			// }
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			KXTApplication.isLoadedImgError = true;
			Toast.makeText(context, "表情初始化失败", Toast.LENGTH_SHORT).show();
		}
		return null;
	}
}
