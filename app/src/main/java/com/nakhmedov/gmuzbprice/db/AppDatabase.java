package com.nakhmedov.gmuzbprice.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.nakhmedov.gmuzbprice.entity.CarModel;
import com.nakhmedov.gmuzbprice.entity.CurrencyInfo;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/7/17
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates
 */

@Database(entities = {CarModel.class, CurrencyInfo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "car_gm_db").build();
        }

        return INSTANCE;
    }

    public abstract CarDao carDao();
    public abstract CurrencyDao mCurrencyDao();
}
