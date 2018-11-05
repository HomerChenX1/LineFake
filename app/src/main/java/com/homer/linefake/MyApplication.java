package com.homer.linefake;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private Activity mCurrentActivity = null;
    private static Context mContext; // some one suggests this. But there might is memory leak issue.

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mxContext) {
        mContext = mxContext;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 啟動Stetho
        // Stetho.initializeWithDefaults(this);
    }
}
