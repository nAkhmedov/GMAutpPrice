package com.nakhmedov.gmuzbprice;

import android.arch.lifecycle.LifecycleActivity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nakhmedov.gmuzbprice.constants.PrefLab;

import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/5/17
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates
 */

public class BaseActivity extends LifecycleActivity {

    protected SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);

        String language = pref.getString(PrefLab.CHOOSE_LANGUAGE, "ru");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);

    }

    public int getLayoutResourceId() {
        return 0;
    }

//    public void showBackBtn() {
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setElevation(0.2f);
//    }
//
//    public void hideBackBtn() {
//        getSupportActionBar().setHomeButtonEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//    }

}
