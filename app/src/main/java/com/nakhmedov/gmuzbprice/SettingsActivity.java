package com.nakhmedov.gmuzbprice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nakhmedov.gmuzbprice.fragment.SettingsFragment;

import butterknife.BindView;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/5/17
 * Time: 9:40 PM
 * To change this template use File | Settings | File Templates
 */

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    public int getLayoutResourceId() {
        return R.layout.settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToolbar.setTitle(getString(R.string.action_settings));



        if (savedInstanceState == null) {
            SettingsFragment settingsFragment = new SettingsFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, settingsFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}
