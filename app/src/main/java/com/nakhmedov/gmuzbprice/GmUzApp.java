package com.nakhmedov.gmuzbprice;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/5/17
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates
 */

public class GmUzApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        mContext = getAppContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
}
