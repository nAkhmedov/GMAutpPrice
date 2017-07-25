package com.nakhmedov.gmuzbprice.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nakhmedov.gmuzbprice.entity.CarModel;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/7/17
 * Time: 11:48 PM
 * To change this template use File | Settings | File Templates
 */

@Dao
public interface CarDao {

    @Query("SELECT type FROM " + CarModel.TABLE_NAME + " group by type")
    LiveData<List<String>> getCarList();

    @Query("SELECT * FROM " + CarModel.TABLE_NAME + " WHERE car_name LIKE :first LIMIT 1")
    CarModel findByName(String first);

    @Insert(onConflict = REPLACE)
    void insertCar(CarModel car);

    @Insert(onConflict = REPLACE)
    void insertCars(List<CarModel> cars);

    @Query("DELETE FROM " + CarModel.TABLE_NAME)
    void delete();

    @Update
    void updateCar(CarModel param);

    @Update
    void updateCars(List<CarModel> param);

    @Query("SELECT id FROM " + CarModel.TABLE_NAME + " WHERE car_name LIKE :name LIMIT 1")
    LiveData<Integer> getCarIdByName(String name);

    @Query("Select * FROM " + CarModel.TABLE_NAME + " WHERE type LIKE :carName ORDER BY car_price ASC")
    LiveData<List<CarModel>> getCarPositionsBy(String carName);

    @Query("SELECT count(*) FROM " + CarModel.TABLE_NAME)
    LiveData<Integer> isDbEmpty();
}
