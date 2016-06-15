package com.jyh.hjtzdxt.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jyh.hjtzdxt.R;
import com.jyh.hjtzdxt.bean.ChatMsgEntity;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.customtool.MySpannableString;
import com.jyh.hjtzdxt.sqlte.SCDataSqlte;
import com.jyh.hjtzdxt.tool.DateTimeUtil;
import com.jyh.hjtzdxt.tool.ImageDownLoader;
import com.jyh.hjtzdxt.tool.ImageMemoryCache;

/**
 * 
 ******************************************
 * @author
 * @文件名称 : ChatMsgAdapter.java
 * @创建时间 : 2013-1-27 下午02:33:16
 * @文件描述 : 消息数据填充起
 ******************************************
 */
public class ChatMsgAdapter extends BaseAdapter implements OnScrollListener {
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
	}

	public List<ChatMsgEntity> getColl() {
		return coll;
	}

	public void setColl(List<ChatMsgEntity> coll) {
		this.coll = coll;
	}

	/** 数据源 */
	private ImageDownLoader loader;
	/** 判定是否第一次加载 */
	private boolean isFirstEnter = true;
	/** 第一张可见Item下标 */
	private int firstVisibleItem;
	/** 每屏Item可见数 */
	private int visibleItemCount;
	private SCDataSqlte dataSqlte;
	protected long mAnimationTime = 150;
	private List<ChatMsgEntity> coll;
	private LayoutInflater mInflater;
	private Context context;
	private HashMap<String, String> hashMap;
	private String rolepath = null;
	private String imagepath = null;
	private ListView listView;
	private String imgBase;
	private Handler handler;
	private boolean isMove;
	private Resources resources;
	private int start;
	private String caitiao;
	private int id;

	private ImageMemoryCache cache = new ImageMemoryCache();
	private MySpannableString mySpannableString;

	public ChatMsgAdapter(Context context, List<ChatMsgEntity> coll, ListView listView) {
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.listView = listView;
		handler = new Handler();
		imgBase = context.getSharedPreferences("appinfo", Context.MODE_PRIVATE).getString("images_url", "http://www.ytx007.com/Uploads/");
		loader = new ImageDownLoader(context);
		hashMap = new HashMap<String, String>();
		this.listView.setOnScrollListener(this);
		InitSqlData();

		mySpannableString = MySpannableString.getInstance(context);
	}

	private void InitSqlData() {
		// TODO Auto-generated method stub
		dataSqlte = new SCDataSqlte(context);
		SQLiteDatabase db = dataSqlte.getReadableDatabase();
		if (db == null) {
			return;
		}
		Cursor cursor = db.query("roomrole", null, null, null, null, null, null);
		String roomroleid = null;
		String image = null;
		while (cursor.moveToNext()) {
			roomroleid = cursor.getString(cursor.getColumnIndex("id"));
			image = cursor.getString(cursor.getColumnIndex("image"));
			hashMap.put(roomroleid, image);
		}
		cursor.close();
		db.close();
	}

	public int getCount() {
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		if (null == coll.get(position))
			return 0;
		String content = coll.get(position).getData();
		if (content.contains(":caitiao[")) {
			return 1;
		}

		else
			return 0;
	}

	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;// 否
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMsgEntity entity = coll.get(position);
		if (entity == null) {
			TextView textView = new TextView(context);
			textView.setHeight(0);
			textView.setVisibility(View.GONE);
			((ListView) parent).setDividerHeight(0);
			return textView;
		}
		ViewHolder viewHolder = null;
		ViewHolder2 viewHolder2 = null;
		int type = getItemViewType(position);
		Log.i("type", "" + type);
		String time = "";
		if (coll.get(position).getTime() != null)
			time = DateTimeUtil.parseMillis2(coll.get(position).getTime() + "000");

		if (convertView == null) {
			switch (type) {
			case 0:
				convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);

				viewHolder = new ViewHolder();
				viewHolder.tvContent = (LinearLayout) convertView.findViewById(R.id.textcontent_chat_fragment);
				viewHolder.tvname = (TextView) convertView.findViewById(R.id.textname_chat_fragment);
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_chat_fragment);
				viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.img_chat_fragment2);
				viewHolder.duiimageView = (ImageView) convertView.findViewById(R.id.dui_chat_fragment);
				viewHolder.tvToName = (TextView) convertView.findViewById(R.id.toname_chat_fragment);
				viewHolder.time = (TextView) convertView.findViewById(R.id.chat_time);
				viewHolder.imageView2.setVisibility(View.GONE);
				convertView.setTag(viewHolder);
				break;
			case 1:
				// case 2:
				convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left2, null);

				viewHolder2 = new ViewHolder2();
				viewHolder2.tvContent = (SimpleDraweeView) convertView.findViewById(R.id.textcontent_chat_fragment);
				viewHolder2.tvname = (TextView) convertView.findViewById(R.id.textname_chat_fragment);
				viewHolder2.imageView = (ImageView) convertView.findViewById(R.id.img_chat_fragment);
				viewHolder2.imageView2 = (ImageView) convertView.findViewById(R.id.img_chat_fragment2);
				viewHolder2.duiimageView = (ImageView) convertView.findViewById(R.id.dui_chat_fragment);
				viewHolder2.tvToName = (TextView) convertView.findViewById(R.id.toname_chat_fragment);
				viewHolder2.time = (TextView) convertView.findViewById(R.id.chat_time);
				viewHolder2.imageView2.setVisibility(View.GONE);
				convertView.setTag(viewHolder2);
				break;
			}
		} else {
			switch (type) {
			case 0:
				try {
					viewHolder = (ViewHolder) convertView.getTag();
					viewHolder.imageView2.setVisibility(View.GONE);
				} catch (Exception e) {
					convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);

					viewHolder = new ViewHolder();
					viewHolder.tvContent = (LinearLayout) convertView.findViewById(R.id.textcontent_chat_fragment);
					viewHolder.tvname = (TextView) convertView.findViewById(R.id.textname_chat_fragment);
					viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_chat_fragment);
					viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.img_chat_fragment2);
					viewHolder.duiimageView = (ImageView) convertView.findViewById(R.id.dui_chat_fragment);
					viewHolder.tvToName = (TextView) convertView.findViewById(R.id.toname_chat_fragment);
					viewHolder.time = (TextView) convertView.findViewById(R.id.chat_time);
					viewHolder.imageView2.setVisibility(View.GONE);
					convertView.setTag(viewHolder);
				}
				break;
			case 1:
				// case 2:
				viewHolder2 = (ViewHolder2) convertView.getTag();
				viewHolder2.imageView2.setVisibility(View.GONE);
				break;
			}
		}
		boolean isTo = false;
		switch (type) {
		case 0:
			viewHolder.tvContent.setTag(entity);
			try {
				mySpannableString.initView(viewHolder.tvContent, context, entity.getData());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (coll.get(position).getT_name() == null || "".equals(coll.get(position).getT_name())) {
				isTo = false;
				viewHolder.tvname.setText(coll.get(position).getF_name() + " ");
				viewHolder.tvToName.setVisibility(View.GONE);
				viewHolder.duiimageView.setVisibility(View.GONE);
			} else {
				isTo = true;
				viewHolder.tvname.setText(coll.get(position).getF_name() + " ");
				viewHolder.tvToName.setVisibility(View.GONE);
				viewHolder.duiimageView.setVisibility(View.GONE);
				viewHolder.tvToName.setText(coll.get(position).getT_name());
			}
			viewHolder.imageView.setTag(hashMap.get(coll.get(position).getF_rid()));
			if (cache.get(hashMap.get(coll.get(position).getF_rid())) != null)
				viewHolder.imageView.setImageBitmap(cache.get(hashMap.get(coll.get(position).getF_rid())));
			else {
				viewHolder.imageView.setImageResource(R.drawable.icon_17yk);
				new imgUtils(viewHolder.imageView, hashMap.get(coll.get(position).getF_rid())).execute();
			}
			viewHolder.imageView2.setVisibility(View.GONE);
			viewHolder.time.setText(time);
			// }
			break;
		case 1:
			resources = context.getResources();
			viewHolder2.imageView2.setVisibility(View.GONE);
			viewHolder2.tvContent.setTag(entity);
			try {
				start = (entity.getData().indexOf("/:caitiao[") + 10);
				caitiao = entity.getData().substring(start, entity.getData().indexOf("]", start));
				Log.i("caitiao", caitiao);
				String s = ((KXTApplication) context.getApplicationContext()).getEmojiMaps().get(caitiao);
				//设置图片缩放方式
				GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setActualImageScaleType(
						ScaleType.FIT_XY).build();
				viewHolder2.tvContent.setHierarchy(hierarchy);
				DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(Uri.parse(s)).setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
						.build();
				viewHolder2.tvContent.setController(draweeController);
			} catch (Exception e) {
				// TODO: handle exception
				viewHolder2.tvContent.setImageBitmap(null);
				e.printStackTrace();
			}
			viewHolder2.tvname.setText(coll.get(position).getF_name() + "");
			viewHolder2.tvToName.setVisibility(View.GONE);
			viewHolder2.duiimageView.setVisibility(View.GONE);
			viewHolder2.time.setText(time);
			viewHolder2.imageView.setTag(hashMap.get(coll.get(position).getF_rid()));
			if (cache.get(hashMap.get(coll.get(position).getF_rid())) != null)
				viewHolder2.imageView.setImageBitmap(cache.get(hashMap.get(coll.get(position).getF_rid())));
			else {
				viewHolder2.imageView.setImageResource(R.drawable.icon_17yk);
				new imgUtils(viewHolder2.imageView, hashMap.get(coll.get(position).getF_rid())).execute();
			}
			break;
		}
		return convertView;
	}

	class imgUtils extends AsyncTask<Void, Void, Bitmap> {

		private ImageView img;
		private String url;

		public imgUtils(ImageView img, String url) {
			super();
			this.img = img;
			this.url = url;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			if (url == null)
				return null;
			Bitmap bitmap = loader.getBitmapCache(url);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				img.setImageBitmap(result);
				cache.put(url, result);
			} else {
				img.setImageResource(R.drawable.empty_img);
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当停止滚动时，加载图片
		// isInit = true;
		if (scrollState == SCROLL_STATE_IDLE) {
			isMove = false;
			loadImage(firstVisibleItem, visibleItemCount);
			Log.i("info", "SCROLL_1");
			// }
		} else {
			Log.i("info", "SCROLL_2");
			isMove = true;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.visibleItemCount = visibleItemCount;
	}

	/**
	 * 加载图片，若缓存中没有，则根据url下载
	 * 
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	@SuppressWarnings("deprecation")
	private void loadImage(int firstVisibleItem, int visibleItemCount) {
		Bitmap bitmap = null;
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			if (hashMap.isEmpty()) {
				Log.i("containsKey1", "......");
				InitSqlData();
			}
			String url = null;
			try {
				url = hashMap.get(coll.get(i).getF_rid());
				final ImageView imageView = (ImageView) listView.findViewWithTag(url);
				Log.i("info", "img==null?" + (imageView == null));
				new imgUtils(imageView, url).execute();
				bitmap = loader.getBitmapCache(url);
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
					// 防止滚动时多次下载
					if (loader.getTaskCollection().containsKey(url)) {
						continue;
					}
					if (imageView == null)
						return;
					imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.empty_img));
					loader.loadImage(url, imageView.getWidth(), imageView.getHeight(), new ImageDownLoader.AsyncImageLoaderListener() {
						@Override
						public void onImageLoader(Bitmap bitmap) {
							if (imageView != null && bitmap != null) {
								imageView.setImageBitmap(bitmap);
							}
						}
					});
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTasks() {
		loader.cancelTasks();
	}

	public class ViewHolder {
		public LinearLayout tvContent;
		public ImageView imageView, imageView2;
		public TextView tvname;
		public ImageView duiimageView;
		public TextView tvToName, time;
	}

	public class ViewHolder2 {
		public SimpleDraweeView tvContent;
		public ImageView imageView, imageView2;
		public TextView tvname;
		public ImageView duiimageView;
		public TextView tvToName, time;
	}

}
