package com.jyh.hjtzdxt.customtool;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.jyh.hjtzdxt.GotyeLiveActivity;

/**
 * @author beginner
 * @date 创建时间：2015年8月14日 上午10:58:28
 * @version 1.0
 */
public class MyListView extends ListView {
	private Close close;
	private Context context;

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public MyListView(Context context) {
		super(context);
		this.context = context;
	}

	public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// new FaceRelativeLayout(getContext()).close();
		int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
//			FaceRelativeLayout faceRelativeLayout = ((LiveActivity) context)
//					.getFragment().getMyView();
//			if (faceRelativeLayout!=null&&faceRelativeLayout.getviewshow())
//				faceRelativeLayout.close();
			FaceRelativeLayout.layout.close();
			if(context instanceof GotyeLiveActivity){
				((GotyeLiveActivity)context).videoVisible();
			}
		}
		return super.onTouchEvent(ev);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}
	
	interface Close {
		public void close();
	}
}
