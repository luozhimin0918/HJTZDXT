package com.jyh.hjtzdxt.socket;


import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MyWindowManager {
    private  static MyWindowManager floatManager;
    //全局的上下文环境
    private static Context mContext;
    //FloatView
    private FloatWindowSmallView floatView;
    //全局的LayoutParams
    private WindowManager.LayoutParams params;


    private MyWindowManager(){
        //获取WindowManager实例对象
        getWindowManager();
    }
    /**
     * 构建单例模式
     */
    public static MyWindowManager getInstance(Context context){
        mContext = context;
        if(floatManager == null){
            floatManager = new MyWindowManager();
        }
        return floatManager;
    }

    public WindowManager getWindowManager(){
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return windowManager;
    }

    /**
     * 初始化windowManager的参数
     */
    public void init(){
        if(getWindowManager() != null){ //设置WindowManager的参数
            params = new WindowManager.LayoutParams();
           //设置参数
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; //type
            params.format =  PixelFormat.RGBA_8888; //背景的透明度;
            //控制是否可以触摸Window，还可以设置焦点
            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE
                    | LayoutParams.FLAG_NOT_TOUCHABLE; //不获取焦点

            params.gravity = Gravity.LEFT|Gravity.TOP; // 调整悬浮窗口
            // 以屏幕左上角为原点，设置x、y初始值
            params.x = 0;
            params.y = 0;

            params.width =1; 
            params.height = 1; 
            //动态的添加
            floatView = new FloatWindowSmallView(mContext);
            floatView.setWindowManager(getWindowManager());
            //在WindowManager实际添加的View,自己设定的params
            floatView.setParams(params);
            getWindowManager().addView(floatView,params);
        }
    }

    //定义移除的方法
    public void remove(){
        if(getWindowManager() != null && floatView != null){
            getWindowManager().removeView(floatView);
        }
    }

}
