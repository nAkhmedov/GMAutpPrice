package com.nakhmedov.gmuzbprice.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.nakhmedov.gmuzbprice.entity.CurrencyInfo;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/29/17
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates
 */

@Dao
public interface CurrencyDao {
    @Query("SELECT * FROM " + CurrencyInfo.TABLE_NAME)
    LiveData<CurrencyInfo> getCurrencyInfo();

    @Insert(onConflict = REPLACE)
    void insertCurrencyInfo(CurrencyInfo info);
}
