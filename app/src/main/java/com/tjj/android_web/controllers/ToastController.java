package com.tjj.android_web.controllers;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.tjj.android_web.utils.JSBridge;


/**
 * 扫描控制器
 */
public class ToastController {
    private Context mContext;
    private Handler mHandler;
    public ToastController(Context context){
        mContext = context;
        mHandler = new Handler();
    }
    public void shortToast(JSBridge.Request request){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, request.getDataAsString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void longToast(JSBridge.Request request){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, request.getDataAsString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
