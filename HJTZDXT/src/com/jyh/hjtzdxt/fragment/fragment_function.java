package com.jyh.hjtzdxt.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jyh.hjtzdxt.Login_One;
import com.jyh.hjtzdxt.MineActivity;
import com.jyh.hjtzdxt.R;
import com.jyh.hjtzdxt.Register_One;
import com.jyh.hjtzdxt.WebActivity;
import com.jyh.hjtzdxt._Activity;
import com.jyh.hjtzdxt.tool.DisplayUtilJYH;

/**
 * @author beginner
 * @date 创建时间：2015年7月23日 下午2:47:19
 * @version 1.0
 */
public class fragment_function extends Fragment implements OnItemClickListener {

	private GridView gridView;
	private Intent intent, intent2, intent3, intent4, intent5;
	private SharedPreferences preferences;
	private SharedPreferences p;
	private String token;
	private long time;
	private boolean isChange;// 判断是否更改功能图标
	private changetitle listener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_function, null);
		gridView = (GridView) view.findViewById(R.id.gridViewId);

		gridView.setAdapter(new MyAdapter(getActivity()));
		gridView.setOnItemClickListener(this);
		intent = new Intent(getActivity(), WebActivity.class);
		intent5 = new Intent(getActivity(), _Activity.class);
		preferences = getActivity().getSharedPreferences("appinfo", Context.MODE_PRIVATE);
		p = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
		token = p.getString("token", null);
		time = p.getLong("expired_time", 0);
		return view;
	}

	// 自定义适配器
	class MyAdapter extends BaseAdapter {
		// 上下文对象
		private Context context;
		// 图片数组
		private Integer[] imgs = { R.drawable.function4, R.drawable.function1, R.drawable.function3, R.drawable.function2,
				R.drawable.function5, R.drawable.function6, R.drawable.function9, R.drawable.function10, R.drawable.function7 };
		private Integer[] imgs2 = { R.drawable.function4, R.drawable.function1, R.drawable.function3, R.drawable.function2,
				R.drawable.function5, R.drawable.function6, R.drawable.function9, R.drawable.function8, R.drawable.function7 };

		MyAdapter(Context context) {
			this.context = context;
		}

		public int getCount() {
			return imgs.length;
		}

		public Object getItem(int item) {
			return item;
		}

		public long getItemId(int id) {
			return id;
		}

		// 创建View方法
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView imageView;
			View view = null;
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_function, null);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					(int) ((gridView.getHeight() - 10 * DisplayUtilJYH.getDpi(getActivity())) / 3));
			imageView = (ImageView) convertView.findViewById(R.id.img);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);// 设置刻度的类型
			convertView.setLayoutParams(params);
			if (token != null && time > System.currentTimeMillis() / 1000)
				imageView.setImageResource(imgs2[position]);// 为ImageView设置图片资源
			else
				imageView.setImageResource(imgs[position]);
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		switch (position) {
		case 1:
			Log.i("GridView", "行情");
			// 行情
			listener.isChange(true);
			intent5.putExtra("type", 2);
			intent5.putExtra("from", "live");
			startActivity(intent5);
			break;
		case 3:
//			Log.i("GridView", "数据");
//			listener.isChange(true);
//			intent.putExtra("title", "数据");
//			intent.putExtra("url", preferences.getString("fn_nav_url", null));
//			startActivity(intent);
			listener.isChange(true);
			intent5.putExtra("type", 4);
			intent5.putExtra("from", "live");
			startActivity(intent5);
			// 数据
			break;
		case 2:
			Log.i("GridView", "日历");
			listener.isChange(true);
			intent5.putExtra("type", 3);
			intent5.putExtra("from", "live");
			startActivity(intent5);
			// 日历
			break;
		case 0:
			Log.i("GridView", "快讯");
			listener.isChange(true);
			intent5.putExtra("type", 1);
			intent5.putExtra("from", "live");
			startActivity(intent5);
			// 快讯
			break;
		case 4:
//			Log.i("GridView", "课程");
//			listener.isChange(true);
//			// 课程
//			intent.putExtra("url", preferences.getString("course_url", null));
//			startActivity(intent);
			listener.isChange(true);
			intent5.putExtra("type", 5);
			intent5.putExtra("from", "live");
			startActivity(intent5);
			break;
		case 5:
//			Log.i("GridView", "公告");
//			listener.isChange(true);
//			intent.putExtra("url", preferences.getString("bulletin_url", null));
//			startActivity(intent);
			listener.isChange(true);
			intent5.putExtra("type", 6);
			intent5.putExtra("from", "live");
			startActivity(intent5);
			// 公告
			break;

		case 6:
			Log.i("GridView", "注册");
			// 注册
			listener.isChange(true);
			intent2 = new Intent(getActivity(), Register_One.class);
			intent2.putExtra("from", "live");
			startActivity(intent2);
			break;
		case 7:
			Log.i("GridView", "登录");
			// 登录
			listener.isChange(true);
			if (token != null && time > System.currentTimeMillis() / 1000) {
				intent4 = new Intent(getActivity(), MineActivity.class);
				startActivity(intent4);
			} else {
				intent3 = new Intent(getActivity(), Login_One.class);
				intent3.putExtra("from", "live");
				startActivity(intent3);
			}
			break;
		case 8:
			listener.isChange(false);
			getActivity().finish();
			break;
		default:
			Log.i("GridView", "" + position);
			break;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (changetitle) activity;
		} catch (Exception e) {
		}
	}

	public interface changetitle {
		public void isChange(boolean ischange);
	}
}
