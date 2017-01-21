package com.omebee.android.beeschat;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

/**
 * Created by phannguyen on 9/12/16.
 */
public class MyApplication extends Application {
    private static Context mAppContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "p4ZDLMbxW2Dj5JhAOuYT0cpZrLjfKjXJBVJrz6Yc", "HpxXFVSsSpA7GItZKH1hbiCtP74nNyvxuSZAzOJf");
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAppContext = this.getApplicationContext();
    }

    public static Context getAppContext(){
        return mAppContext;
    }
}
