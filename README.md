# TJJ-Android-Web
将android作为服务器,webview作为访问端,实现mvc操作的实践
# 用法
### 1. 自定义一个Test类
'''
Class Test{
    public void test(JSBridge.Request request){
        JSBridge.Response response = new JSBridge.Response();
        response.setMessage("后台获取的数据进行返回").setStatus(400).setData("1234123");
        request.callback(response);
    }
}
'''
### 2. MainActivity注册Test类
'''
JSBridge mJSBridge = new JSBridge(mContext,findViewById(R.id.view_container));
mJSBridge.registerJSObject("test",new Test());
'''
### 3. js内调用Test类里的test方法
'''
JSBridge.request('test.test',{test:123},function(response){
    console.log(response.status);
    console.log(response.message);
    console.log(response.data);
})
'''
# 已实现
#### 自定义JSBridge
#### 实现js对Toast的操作
#### 实现js对设备基础信息的获取