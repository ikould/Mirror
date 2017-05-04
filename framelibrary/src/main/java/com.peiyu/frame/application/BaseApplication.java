package com.peiyu.frame.application;

import android.app.Application;

import com.peiyu.frame.handler.CrashFileSaveListener;
import com.peiyu.frame.handler.CrashHandler;


/**
 * Description  实现自定义异常处理的Application
 * Created by chenqiao on 2015/9/21.
 */
public abstract class BaseApplication extends Application implements CrashFileSaveListener {

    protected CrashHandler crashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        onBaseCreate();
    }

    protected void setCrashHandlerEnable(boolean tf) {
        if (tf) {
            /**
             * 设置默认异常处理Handler
             */
            crashHandler = CrashHandler.getInstance(this);
            crashHandler.init(getApplicationContext());
        }
    }

    protected abstract void onBaseCreate();

    @Override
    public abstract void crashFileSaveTo(String filePath);
}
