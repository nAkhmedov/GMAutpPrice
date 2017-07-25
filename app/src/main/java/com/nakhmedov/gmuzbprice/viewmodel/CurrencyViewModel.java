package com.nakhmedov.gmuzbprice.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.nakhmedov.gmuzbprice.db.AppDatabase;
import com.nakhmedov.gmuzbprice.entity.CurrencyInfo;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/29/17
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates
 */

public class CurrencyViewModel extends AndroidViewModel {

    private final AppDatabase appDatabase;

    public CurrencyViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public void insertItem(CurrencyInfo currencyInfoModel) {
        new insertCarAsyncTask(appDatabase).execute(currencyInfoModel);
    }

    public LiveData<CurrencyInfo> getInfo() {
        return appDatabase.mCurrencyDao().getCurrencyInfo();
    }

    private class insertCarAsyncTask extends AsyncTask<CurrencyInfo, Void, Void> {
        private final AppDatabase db;

        public insertCarAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(CurrencyInfo... params) {
            db.mCurrencyDao().insertCurrencyInfo(params[0]);
            return null;
        }
    }
}
