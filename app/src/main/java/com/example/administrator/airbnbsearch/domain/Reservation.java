package com.example.administrator.airbnbsearch.domain;

/**
 * Created by Administrator on 2017-07-27.
 */

public class Reservation {

    // 여기서만 쓰이기 때문에 굳이 Const 로 빼주지 않아도 된다.
    public static final int TYPE_ONE = 10;
    public static final int TYPE_TWO = 20;
    public static final int TYPE_THREE = 30;

    public static final String AM_WIFI = "wifi";
    public static final String AM_AIRCON = "aircon";
    public static final String AM_REFRIGE = "refriger";
    public static final String AM_PARKING = "parking";
    public static final String AM_ELEVATOR = "elevator";

    public String checkIn;
    public String checkOut;

    public int guest;
    public int type;
    public int price_min;
    public int price_max;

    public int[] amenity;

}
