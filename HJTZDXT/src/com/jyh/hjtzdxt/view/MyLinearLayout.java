package com.jyh.hjtzdxt.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.jyh.hjtzdxt.tool.DisplayUtilJYH;

/**
 * 用以解决软键盘问题
 * 
 * @author Administrator
 *
 */
public class MyLinearLayout extends LinearLayout {

	private int height;
	private Context context;
	private float dpi;

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	private void init(Context context) {
		this.context = context;
		dpi = DisplayUtilJYH.getDpi((Activity) context);
		Log.i("info", "dpi="+dpi);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		height = h > oldh ? h : oldh;
		getChildAt(0).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) ((height - 41 * dpi) / 2.75+0.5f)));
		getChildAt(1).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (40 * dpi+0.5f)));
		getChildAt(2).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (dpi+0.5f)));
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}

}
