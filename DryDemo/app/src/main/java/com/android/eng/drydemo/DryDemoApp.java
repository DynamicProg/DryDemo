package com.android.eng.drydemo;

import android.app.Application;
import android.content.Context;

import com.android.eng.drydemo.Utils.CrashHandler;

/**
 * Created by Eng on 2018/5/22.
 */

public class DryDemoApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CrashHandler.getsInstance().init(this);
    }

    public static DryDemoApp getContext() {
        return (DryDemoApp) mContext;
    }
}
