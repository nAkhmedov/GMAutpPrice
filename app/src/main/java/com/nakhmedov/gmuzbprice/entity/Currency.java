package com.nakhmedov.gmuzbprice.entity;

import android.arch.persistence.room.Entity;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 6/15/17
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates
 */

@Root(name = "CBU_Curr", strict = false)
public class Currency {

    @ElementList(inline = true, required = false)
    private List<CurrencyInfo> list;

    @Attribute
    private String name;

    public String getName() {
        return name;
    }

    public List<CurrencyInfo> getProperties() {
        return list;
    }

}

