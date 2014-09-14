package com.michaldabski.panoramio.requests;

import com.android.volley.Response;

/**
 * Created by Michal on 10/08/2014.
 */
public class NearbyPhotosRequest extends PanoramioRequest
{
    public static final int NUM_PHOTOS = 50;
    private static final float
            LAT_MULTIPLIER = 0.4f,
            LON_MULTIPLIER = 1f;
    private final float userLat, userLong;

    public NearbyPhotosRequest(Response.ErrorListener listener, float latitude, float longitude, int from, int to, float distance)
    {
        super(listener, longitude-(distance* LON_MULTIPLIER), latitude-(distance* LAT_MULTIPLIER), longitude+(distance*LON_MULTIPLIER), latitude+(distance*LAT_MULTIPLIER), from, to);
        userLat = latitude;
        userLong = longitude;
    }

    public NearbyPhotosRequest(Response.ErrorListener listener, float latitude, float longitude, int from, float distance)
    {
        this(listener, latitude, longitude, from, from+NUM_PHOTOS, distance);
    }

}
