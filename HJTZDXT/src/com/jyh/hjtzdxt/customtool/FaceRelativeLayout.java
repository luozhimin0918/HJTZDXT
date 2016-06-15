package com.jyh.hjtzdxt.customtool;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jyh.hjtzdxt.R;
import com.jyh.hjtzdxt.adapter.FaceAdapter_New;
import com.jyh.hjtzdxt.adapter.ViewPagerAdapter;
import com.jyh.hjtzdxt.bean.ChatEmoji_New;
import com.jyh.hjtzdxt.bean.KXTApplication;
import com.jyh.hjtzdxt.customtool.MyListView.Close;
import com.jyh.hjtzdxt.tool.FaceConversionUtil;

/**
 * 
 ******************************************
 * @author
 * @文件名称 : FaceRelativeLayout.java
 * @创建时间 : 2013-1-27 下午02:34:17
 * @文件描述 : 带表情的自定义输入框
 ******************************************
 */
public class FaceRelativeLayout extends RelativeLayout implements OnItemClickListener, OnClickListener, Close {
	FaceAdapter_New adapter = null;
	public static FaceRelativeLayout layout;
	private Context context;
	private boolean IsCaiTiao = true;
	/** 表情页的监听事件 */
	private OnCorpusSelectedListener mListener;

	/** 显示表情页的viewpager */
	private ViewPager vp_face;

	/** 表情页界面集合 */
	private ArrayList<View> pageViews;

	/** 游标显示布局 */
	private LinearLayout layout_point;

	/** 游标点集合 */
	private ArrayList<ImageView> pointViews;

	private InputMethodManager imm;
	/** 表情区域 */
	private View view;

	/** 输入框 */
	private EditText et_sendmessage;

	/** 表情数据填充器 */
	private List<FaceAdapter_New> faceAdapters;

	/** 当前表情页 */
	private int current = 0;
	private Timer timer;
	private boolean cansend = true;
	private int i;

	private RecyclerView recyclerView;
	private KXTApplication application;

	public FaceRelativeLayout(Context context) {
		super(context);
		this.context = context;
		layout = this;
		i = Integer.parseInt(context.getSharedPreferences("userinfo", context.MODE_PRIVATE).getString("limit_colorbar_time", "15")) + 1;
	}

	public FaceRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		layout = this;
		i = Integer.parseInt(context.getSharedPreferences("userinfo", context.MODE_PRIVATE).getString("limit_colorbar_time", "15")) + 1;
	}

	public FaceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		layout = this;
	}

	public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
		mListener = listener;
	}

	/**
	 * 表情选择监听
	 * 
	 * @author naibo-liao
	 * @时间： 2013-1-15下午04:32:54
	 */
	public interface OnCorpusSelectedListener {

		void onCorpusSelected(ChatEmoji_New emoji);

		void onCorpusDeleted();
	}

	@Override
	protected void onFinishInflate() {// 获取解析后的数据
		super.onFinishInflate();
		List<List<ChatEmoji_New>> emojisdf = new ArrayList<List<ChatEmoji_New>>();
		onCreate(emojisdf);
		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	private void onCreate(List<List<ChatEmoji_New>> emojisdf) {
		Init_View();
		Init_viewPager(emojisdf, IsCaiTiao);
		Init_Point();
		Init_Data();
	}

	private Handler handle=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			KXTApplication.isLoadedImg=true;
			return false;
		}
	});
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_face:
			// 隐藏表情选择框
			if (KXTApplication.isLoadedImgError) {
				Toast.makeText(context, "表情加载异常,重新加载中", 0).show();
				KXTApplication.isLoadedImgError=false;
				KXTApplication.isLoadedImg=false;
				new Runnable() {
					public void run() {
						for(int i=0;i<application.emojiNum;i++){
							FaceConversionUtil.getInstace().emoji.get(i).clear();
							FaceConversionUtil.getInstace().emojiList.get(i).clear();
							FaceConversionUtil.getInstace().getFileText(application, i);
						}
						handle.sendEmptyMessage(0);
					}
				}.run();
				return;
			}
			if (!KXTApplication.isLoadedImg) {
				Toast.makeText(getContext(), "表情包正在加载中。。。", 0).show();

			} else {
				if (list != null) {
					list.set(0, true);
					for (int i = 1; i < len; i++) {
						list.set(i, false);
					}
				}
				mAdapter.notifyDataSetChanged();
				List<List<ChatEmoji_New>> emojiscaitiao = FaceConversionUtil.getInstace().emojiList.get(0);
				IsCaiTiao = emojiscaitiao.get(0).get(0).isCaitiao();
				Init_viewPager(emojiscaitiao, IsCaiTiao);
				adapter.notifyDataSetChanged();
				layout_point.removeAllViews();
				Init_Point();
				Init_Data();

				if (view.getVisibility() == View.VISIBLE) {
					view.setVisibility(View.GONE);
				} else {
					view.setVisibility(View.VISIBLE);
					boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
					if (isOpen) {
						((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
								view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
			}
			break;
		case R.id.et_sendmessage:
			// 隐藏表情选择框
			if (view.getVisibility() == View.VISIBLE) {
				view.setVisibility(View.GONE);
			}
			break;
		}
	}

	/**
	 * 隐藏表情选择框
	 */
	public boolean hideFaceView() {
		// 隐藏表情选择框
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	/**
	 * 初始化控件
	 */
	private void Init_View() {
		vp_face = (ViewPager) findViewById(R.id.vp_contains);
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		layout_point = (LinearLayout) findViewById(R.id.iv_image);
		et_sendmessage.setOnClickListener(this);
		findViewById(R.id.btn_face).setOnClickListener(this);

		application = (KXTApplication) context.getApplicationContext();

		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
		// 垂直方向
		mLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
		// 给RecyclerView设置布局管理器
		len = application.emojiNum;
		recyclerView.setLayoutManager(mLayoutManager);
		list = new ArrayList<Boolean>();

		for (int i = 0; i < len; i++) {
			if (i == 0)
				list.add(true);
			else
				list.add(false);
		}

		mAdapter = new emojiAdapter(list);
		mAdapter.setOnItemClickLitener(new OnItemClickLitener() {

			@Override
			public void onItemClick(View view, int position) {
				// TODO Auto-generated method stub

				for (int i = 0; i < len; i++) {
					if (i == position)
						list.set(i, true);
					else
						list.set(i, false);
				}
				mAdapter.notifyDataSetChanged();
				List<List<ChatEmoji_New>> emojiscaitiao = FaceConversionUtil.getInstace().emojiList.get(position);
				IsCaiTiao = emojiscaitiao.get(0).get(0).isCaitiao();
				Init_viewPager(emojiscaitiao, IsCaiTiao);
				adapter.notifyDataSetChanged();
				layout_point.removeAllViews();
				Init_Point();
				Init_Data();
			}
		});
		recyclerView.setAdapter(mAdapter);
		view = findViewById(R.id.ll_facechoose);

	}

	/**
	 * 初始化显示表情的viewpager
	 */
	@SuppressWarnings("deprecation")
	private void Init_viewPager(List<List<ChatEmoji_New>> alllist, Boolean isCaitiao) {
		pageViews = new ArrayList<View>();
		// 左侧添加空页
		View nullView1 = new View(context);
		// 设置透明背景
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView1);

		// 中间添加表情页

		faceAdapters = new ArrayList<FaceAdapter_New>();
		for (int i = 0; i < alllist.size(); i++) {
			GridView view = new GridView(context);
			adapter = new FaceAdapter_New(context, alllist.get(i), IsCaiTiao);
			view.setAdapter(adapter);
			faceAdapters.add(adapter);
			view.setOnItemClickListener(this);
			if (isCaitiao) {
				view.setNumColumns(2);
			} else {
				view.setNumColumns(7);
			}
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(8, 0, 5, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			pageViews.add(view);
		}
		// 右侧添加空页面
		View nullView2 = new View(context);
		// 设置透明背景
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView2);
	}

	/**
	 * 初始化游标
	 */
	private void Init_Point() {

		pointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(context);
			imageView.setBackgroundResource(R.drawable.d1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.width = 8;
			layoutParams.height = 8;
			layout_point.addView(imageView, layoutParams);
			if (i == 0 || i == pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.d2);
			}
			pointViews.add(imageView);

		}
	}

	/**
	 * 填充数据
	 */
	private void Init_Data() {
		vp_face.setAdapter(new ViewPagerAdapter(pageViews));

		vp_face.setCurrentItem(1);
		current = 0;
		vp_face.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				current = arg0 - 1;
				// 描绘分页点
				draw_Point(arg0);
				// 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
				if (arg0 == pointViews.size() - 1 || arg0 == 0) {
					if (arg0 == 0) {
						vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
						pointViews.get(1).setBackgroundResource(R.drawable.d2);
					} else {
						vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
						pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.d2);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	/**
	 * 绘制游标背景
	 */
	public void draw_Point(int index) {
		for (int i = 1; i < pointViews.size(); i++) {

			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ChatEmoji_New emoji = (ChatEmoji_New) faceAdapters.get(current).getItem(arg2);
		if (emoji == null || emoji.getImage() == null) {
			return;
		}
		if (emoji.getImage().equals("R.drawable.face_del_icon")) {
			if (et_sendmessage.getText().length() > 2) {
				String a = et_sendmessage.getText().toString()
						.substring(et_sendmessage.getText().length() - 2, et_sendmessage.getText().length() - 1);
				if (a.equals("]")) {
					for (int i = 0; i < 2; i++) {
						DeleteStr();
					}
				} else {
					DeleteStr();
				}
			} else {
				DeleteStr();
			}
		}
		if (!IsCaiTiao) {
			AppendSpanString(emoji);
		} else {
			if (cansend) {
				i = Integer
						.parseInt(context.getSharedPreferences("userinfo", context.MODE_PRIVATE).getString("limit_colorbar_time", "15")) + 1;
				et_sendmessage.setText("");
				timer = new Timer();
				setTimerTask();
				AppendSpanString(emoji);
				Intent intent = new Intent();
				intent.setAction("彩条发送");
				intent.putExtra("send", "send");
				context.sendBroadcast(intent);
				cansend = false;
			} else {
				Toast.makeText(context, "还剩" + i + "秒", 0).show();
			}

		}
	}

	private void setTimerTask() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				IsCanSend();
			}
		}, 1 * 1000, 1 * 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
	}

	protected void IsCanSend() {
		if (i == 0 || i < 0) {
			timer.cancel();
			timer.purge();
			i = Integer.parseInt(context.getSharedPreferences("userinfo", context.MODE_PRIVATE).getString("limit_colorbar_time", "15")) + 1;
			cansend = true;
		}
		i = i - 1;
	}

	private void AppendSpanString(ChatEmoji_New emoji) {
		if (!TextUtils.isEmpty(emoji.getName())) {
			if (mListener != null)
				mListener.onCorpusSelected(emoji);
			SpannableString spannableString = FaceConversionUtil.getInstace().addFace(getContext(), emoji.getImage(), emoji.getPath(),
					emoji.getName());
			et_sendmessage.append(spannableString != null ? spannableString : "");
		}
	}

	private void DeleteStr() {
		int selection = et_sendmessage.getSelectionStart();
		String text = et_sendmessage.getText().toString();
		if (selection > 0) {
			String text2 = text.substring(selection - 1);
			if ("]".equals(text2)) {
				int start = text.lastIndexOf("[");
				int end = selection;
				et_sendmessage.getText().delete(start, end);
				return;
			}
			et_sendmessage.getText().delete(selection - 1, selection);
		}
	}

	@Override
	public void close() {
		// 隐藏表情选择框
		if (view.getVisibility() == View.VISIBLE) {
			view.setVisibility(View.GONE);
		}
	}

	public boolean getviewshow() {
		if (view.getVisibility() == View.GONE)
			return false;
		return true;
	}

	private int len;
	private emojiAdapter mAdapter;
	private List<Boolean> list;

	class emojiAdapter extends Adapter<MyHolder> {

		private OnItemClickLitener mOnItemClickLitener;
		private List<Boolean> list;

		public emojiAdapter(List<Boolean> list) {
			super();
			this.list = list;
		}

		public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
			this.mOnItemClickLitener = mOnItemClickLitener;
		}

		@Override
		public int getItemCount() {
			// TODO Auto-generated method stub
			return len;
		}

		@Override
		public void onBindViewHolder(final MyHolder arg0, final int arg1) {
			// TODO Auto-generated method stub
			String code = application.getChatEmojiTitles().get(arg1).getName();
			code = code != null ? code : "";

			arg0.textView.setSelected(list.get(arg1));
			arg0.textView.setText(code);
			if (mOnItemClickLitener != null) {
				arg0.textView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mOnItemClickLitener.onItemClick(arg0.textView, arg1);
					}
				});
			}
		}

		@Override
		public MyHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			MyHolder holder = new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_emojititle, arg0, false));
			return holder;
		}

	}

	public interface OnItemClickLitener {
		void onItemClick(View view, int position);
	}

	class MyHolder extends ViewHolder {
		private TextView textView;

		public MyHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			textView = (TextView) itemView.findViewById(R.id.emojiTitle_tv);
		}

	}

}
