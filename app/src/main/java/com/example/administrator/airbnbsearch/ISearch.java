package com.example.administrator.airbnbsearch;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017-07-28.
 */

public interface ISearch {

    String SERVER = "http://192.168.10.85/";

    /**
     * @param checkin
     * @param checkout
     * @param guests
     * @param type
     * @param price_min
     * @param price_max
     * @param wifi_exists
     * @return
     */
    @GET("airbnb/house")
    Observable<ResponseBody> get(
            @Query("checkin") String checkin
            , @Query("checkout") String checkout
            , @Query("guests") int guests
            , @Query("type") int type
            , @Query("price_min") int price_min
            , @Query("price_max") int price_max
            , @Query("amenities") int wifi_exists
            );


}
