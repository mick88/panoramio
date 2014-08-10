package com.michaldabski.panoramiotest.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Michal on 08/08/2014.
 */
public class PanoramioResponse
{
    int count;
    @SerializedName("has_more")
    boolean hasMore;
    @SerializedName("map_location")
    MapLocation mapLocation;
    List<Photo> photos;

    public List<Photo> getPhotos()
    {
        return photos;
    }

    public int getCount()
    {
        return count;
    }

    public boolean isHasMore()
    {
        return hasMore;
    }

    public MapLocation getMapLocation()
    {
        return mapLocation;
    }

    @Override
    public String toString()
    {
        return "PanoramioResponse{" +
                "count=" + count +
                ", hasMore=" + hasMore +
                ", mapLocation=" + mapLocation +
                ", photos=" + photos +
                '}';
    }
}
