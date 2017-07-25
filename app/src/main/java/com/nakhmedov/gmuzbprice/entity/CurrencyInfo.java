package com.nakhmedov.gmuzbprice.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/29/17
 * Time: 9:47 AM
 * To change this template use File | Settings | File Templates
 */

@Entity(tableName = CurrencyInfo.TABLE_NAME, indices = {@Index(value = {"name"}, unique = true)})
@Root(name = "CcyNtry", strict = false)
public class CurrencyInfo {

    public static final String TABLE_NAME = "currency";

    @PrimaryKey
    @Attribute(name = "ID", required = false)
    private String id;

    @Element(name="Ccy", required = false)
    private String name;

    @Element(name="Rate", required = false)
    private String rate;

    @Element(name="date", required = false)
    private String date;

    public String getName() {
        return name;
    }

    public String getRate() {
        return rate;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
