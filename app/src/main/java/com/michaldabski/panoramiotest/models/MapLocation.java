package com.michaldabski.panoramiotest.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Michal on 08/08/2014.
 */
public class MapLocation
{
    @SerializedName("lat")
    float latitude;
    @SerializedName("lon")
    float longitude;
    @SerializedName("panoramio_zoom")
    int zoom;

    public float getLatitude()
    {
        return latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

    public int getZoom()
    {
        return zoom;
    }

    @Override
    public String toString()
    {
        return "MapLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", zoom=" + zoom +
                '}';
    }
}
