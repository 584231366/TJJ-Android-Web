package com.tjj.android_web;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tjj.android_web.controllers.ToastController;
import com.tjj.android_web.utils.JSBridge;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Function;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        mContext = this;

        JSBridge mJSBridge = new JSBridge(mContext,findViewById(R.id.view_container));
        mJSBridge.registerJSObject("toast",new ToastController(mContext));
    }
}