package com.homer.linefake;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
/* use this to get currentActivity everywhere, : Activity currentActivity = ((MyApp)context.getApplicationContext()).getCurrentActivity();
*  then get context
*  should merge this file to MyApplication.java
*  */
public class MyBaseActivity extends Activity {
    protected MyApplication mMyApp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyApp = (MyApplication)this.getApplicationContext();
        // Activity currentActivity = ((MyApplication)this.getApplicationContext()).getCurrentActivity();
        // Context a1 = currentActivity.getApplicationContext();
    }
    protected void onResume() {
        super.onResume();
        mMyApp.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = mMyApp.getCurrentActivity();
        if (this.equals(currActivity))
            mMyApp.setCurrentActivity(null);
    }
}
