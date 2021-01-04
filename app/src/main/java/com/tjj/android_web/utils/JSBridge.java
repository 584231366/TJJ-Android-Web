package com.tjj.android_web.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class JSBridge {
    String TAG = "JSBridge";
    Context mContext;
    WebView mWebView;
    Handler mHandler;
    HashMap<String,Object> objects = new HashMap<>();
    @SuppressLint("JavascriptInterface")
    public JSBridge(Context context, WebView webView){
        mContext = context;
        mWebView = webView;
        mHandler = new Handler();

        mWebView.addJavascriptInterface(this, "JSBridge");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                String js = "JSBridge.fns = [];" + // js回调函数存储
                        "JSBridge.request = function(om,data,callback){" + // 统一对android的请求功能
                        "var request = {" +
                        "om: om," +
                        "data: data" +
                        "};" +
                        "if(typeof callback == 'function'){" + // 回调函数存储 并生成一个存储id
                        "this.fns.push(callback);" +
                        "request.callback_id = this.fns.length - 1;" +
                        "};" +
                        "JSBridge.requestAndroid(JSON.stringify(request));" + // 序列化请求参数 向JSBridge类发送请求
                        "};" +
                        "JSBridge.callback = function(response,callback_id){" + // JSBridge响应请求调用对应的回调函数
                        "this.fns[callback_id](response);" +
                        "};";
                // 为JSBridge附加请求和请求回调执行的js处理属性
                mWebView.loadUrl("javascript:"+js);
            }
        });
        mWebView.loadUrl("file:///android_asset/web/index.html");
    }

    /**
     * om参数 格式（object.method） 通过该信息反射调用注册的对象和方法
     * @param str_request
     */
    @JavascriptInterface
    public void requestAndroid(String str_request){
        Request request = new Request(str_request);
        String object_name = request.getString("om").split("\\.")[0];
        String object_method = request.getString("om").split("\\.")[1];
        Response response = new Response();
        Object reflex_obj = objects.get(object_name);
        if(objects.containsKey(object_name)){ // 判断是否存在对应的反射对象
            Method reflex_method = null;
            for(Method method:reflex_obj.getClass().getMethods()){ // 判断是否存在对应的反射方法
                if(method.getName().equals(object_method)){
                    reflex_method = method;
                    break;
                }
            };
            if(reflex_method != null){
                try {
                    reflex_method.invoke(reflex_obj,request);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }else{
                response.setMessage( "控制器内指定"+object_method+"方法的不存在！").setStatus(400);
                request.callback(response);
            }
        }else{
            response.setMessage("控制器"+object_method+"不存在！").setStatus(400);
            request.callback(response);
        }
    }

    /**
     * 注册反射处理对象
     * @param name
     * @param object
     */
    @SuppressLint("JavascriptInterface")
    public void registerJSObject(String name, Object object){
        objects.put(name,object);
    }

    /**
     * 统一请求处理类
     */
    public class Request{
        JSONObject json;
        public Request(String json_str){
            try {
                json = new JSONObject(json_str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public boolean hasCallback(){
            return json.has("callback_id");
        }
        public String getString(String name){
            try {
                return json.getString(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        public JSONObject getData(){
            try {
                return json.getJSONObject("data");
            } catch (JSONException e) {
                return null;
            }
        }
        public void callback(Response response){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 回调处理
                    if(hasCallback()){
                        mWebView.loadUrl("javascript:JSBridge.callback("+response.toString()+","+getString("callback_id")+")");
                    }
                }
            });
        }
    }

    /**
     * 统一回调处理类
     */
    public static class Response{
        JSONObject json;
        public Response(){
            json = new JSONObject();
            try {
                json.put("status",200);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public Response setStatus(int status){
            try {
                json.put("status",status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public Response setMessage(String message){
            try {
                json.put("message",message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public Response setData(JSONObject data){
            try {
                json.put("data",data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public Response setData(String data){
            try {
                json.put("data",data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public String toString(){
            return json.toString();
        }
    }
}
