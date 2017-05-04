package com.ikould.mirror;

import android.os.Handler;
import android.util.Log;

import com.peiyu.frame.application.BaseApplication;

/**
 * describe
 * Created by liudong on 2017/5/3.
 */

public class CoreApplication extends BaseApplication {

    public Handler handler = new Handler();

    private static CoreApplication instance;

    public static CoreApplication getInstance() {
        return instance;
    }

    @Override
    public void crashFileSaveTo(String filePath) {
        Log.d("CoreApplication", "crashFileSaveTo: filePath = " + filePath);
    }

    @Override
    protected void onBaseCreate() {
        instance = this;
    }
}
