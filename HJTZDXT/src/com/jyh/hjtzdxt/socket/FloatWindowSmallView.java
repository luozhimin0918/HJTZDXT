package com.jyh.hjtzdxt.socket;


import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jyh.hjtzdxt.R;

public class FloatWindowSmallView extends ImageView {
    //需要使用WindowManger的工具
    @SuppressWarnings("unused")
	private WindowManager windowManager;
    @SuppressWarnings("unused")
	private WindowManager.LayoutParams params;

    public FloatWindowSmallView(Context context) {
        super(context);
        init();
    }

    public FloatWindowSmallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, //宽度
                        ViewGroup.LayoutParams.WRAP_CONTENT //高度
                )
        );
        setImageResource(R.drawable.out_tag);
    }

    //由外部进行WindowManager的实例化和参数的设定
    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void setParams(WindowManager.LayoutParams params) {
        this.params = params;
    }



    /**
     * 获取电池栏的高度(通过反射获取属性)
     */
    @SuppressWarnings("unused")
	private int  getStatusBarHeight(){
        int bar = 0;
        Class<?> c  = null;
        try {
            c = Class.forName("com.android.internal.R$dimen"); //内部定的的类
            Object object = c.newInstance(); //实例化对象
            Field field = c.getDeclaredField("status_bar_height"); //类里的属性(int status_bar_height)
            int y= Integer.parseInt(field.get(object).toString()); //获取属性默认的值
            bar = getResources().getDimensionPixelSize(y); //转换成像素单位
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return bar;
    }

}
