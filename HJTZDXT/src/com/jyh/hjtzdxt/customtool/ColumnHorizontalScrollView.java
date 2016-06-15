package com.jyh.hjtzdxt.customtool;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class ColumnHorizontalScrollView extends HorizontalScrollView {
	private View ll_content;
	private View ll_more;
	private View rl_column;
	private int mScreenWitdh = 0;
	private Activity activity;

	public ColumnHorizontalScrollView(Context context) {
		super(context);
	}

	public ColumnHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColumnHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		// TODO Auto-generated method stub
		super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		shade_ShowOrHide();
		if (!activity.isFinishing() && ll_content != null && ll_more != null
				&& rl_column != null) {
			if (ll_content.getWidth() <= mScreenWitdh) {
			}
		} else {
			return;
		}
		if (paramInt1 == 0) {
			return;
		}
		if (ll_content.getWidth() - paramInt1 + ll_more.getWidth()
				+ rl_column.getLeft() == mScreenWitdh) {
			return;
		}
	}

	public void setParam(Activity activity, int mScreenWitdh, View paramView1,
			ImageView paramView2, ImageView paramView3, View paramView4,
			View paramView5) {
		this.activity = activity;
		this.mScreenWitdh = mScreenWitdh;
		ll_content = paramView1;
		ll_more = paramView4;
		rl_column = paramView5;
	}

	public void shade_ShowOrHide() {
		if (!activity.isFinishing() && ll_content != null) {
			measure(0, 0);
			if (mScreenWitdh >= getMeasuredWidth()) {
			}
		} else {
			return;
		}
		if (getLeft() == 0) {
			return;
		}
		if (getRight() == getMeasuredWidth() - mScreenWitdh) {
			return;
		}
	}
}
