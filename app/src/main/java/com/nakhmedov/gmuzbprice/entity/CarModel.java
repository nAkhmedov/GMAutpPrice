package com.nakhmedov.gmuzbprice.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/7/17
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates
 */
@IgnoreExtraProperties
@Entity(tableName = CarModel.TABLE_NAME,
        indices = {@Index(value = "car_name", unique = true)})
public class CarModel {

    public static final String TABLE_NAME = "cars";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "car_name")
    private String name;

    @ColumnInfo(name = "car_price")
    private String price;

    private String type;
    private String position;

    public CarModel(String name, String  price, String type, String position) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "CarModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
