package com.nakhmedov.gmuzbprice.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.nakhmedov.gmuzbprice.db.AppDatabase;
import com.nakhmedov.gmuzbprice.entity.CarModel;

import java.util.List;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/12/17
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates
 */

public class CarListViewModel extends AndroidViewModel {

    private final AppDatabase appDatabase;

    public CarListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public LiveData<List<String>> getCarList() {
        return appDatabase.carDao().getCarList();
    }

    public void insertItem(CarModel carModel) {
        new insertCarAsyncTask(appDatabase).execute(carModel);
    }

    public void insertItems(List<CarModel> carModels) {
        new insertCarsAsyncTask(appDatabase).execute(carModels);
    }

    public void updateItem(CarModel carModel) {
        new updateCarTask(appDatabase).execute(carModel);
    }

    public void updateItems(List<CarModel> list) {
        new updateCarsTask(appDatabase).execute(list);
    }

    public LiveData<Integer> getCarId(String carName) {
        return appDatabase.carDao().getCarIdByName(carName);
    }

    public LiveData<List<CarModel>> getCarPositions(String carName) {
        return appDatabase.carDao().getCarPositionsBy(carName);

    }

    public LiveData<Integer> isCarsEmpty() {
        return appDatabase.carDao().isDbEmpty();

    }

    public void deleteItems() {
        new deleteAsyncTask(appDatabase).execute();
    }

    private class insertCarAsyncTask extends AsyncTask<CarModel, Void, Void> {
        private AppDatabase db;

        public insertCarAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(CarModel... params) {
            db.carDao().insertCar(params[0]);
            return null;
        }
    }

    private class insertCarsAsyncTask extends AsyncTask<List<CarModel>, Void, Void> {
        private AppDatabase db;

        public insertCarsAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(List<CarModel>... params) {
            db.carDao().insertCars(params[0]);
            return null;
        }
    }

    private class updateCarTask extends AsyncTask<CarModel, Void, Void> {
        private AppDatabase db;

        public updateCarTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(CarModel... params) {
            db.carDao().updateCar(params[0]);
            return null;
        }
    }

    private class updateCarsTask extends AsyncTask<List<CarModel>, Void, Void> {
        private AppDatabase db;

        public updateCarsTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(List<CarModel>... params) {
            db.carDao().updateCars(params[0]);
            return null;
        }
    }

    private class deleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private AppDatabase db;

        public deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(Void... params) {
            db.carDao().delete();
            return null;
        }
    }
}
