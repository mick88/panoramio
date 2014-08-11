package com.michaldabski.panoramiotest.requests;

import com.android.volley.Response;

/**
 * Created by Michal on 10/08/2014.
 */
public class NearbyPhotosRequest extends PanoramioRequest
{
    public static final int NUM_PHOTOS = 30;
    private final float userLat, userLong;

    public NearbyPhotosRequest(Response.ErrorListener listener, float latitude, float longitude, int from, int to)
    {
        super(listener, longitude-1f, latitude-1f, longitude+1f, latitude+1f, from, to);
        userLat = latitude;
        userLong = longitude;
    }

    public NearbyPhotosRequest(Response.ErrorListener listener, float latitude, float longitude, int from)
    {
        this(listener, latitude, longitude, from, from+NUM_PHOTOS);
    }

}
