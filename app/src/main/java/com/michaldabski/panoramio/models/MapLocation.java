package com.michaldabski.panoramio.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Michal on 08/08/2014.
 */
public class MapLocation implements Parcelable
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeInt(zoom);
    }

    protected void populateFromParcel(Parcel parcel)
    {
        latitude = parcel.readFloat();
        longitude = parcel.readFloat();
        zoom = parcel.readInt();
    }

    public static final Creator<MapLocation> CREATOR = new Creator<MapLocation>()
    {
        @Override
        public MapLocation createFromParcel(Parcel parcel)
        {
            MapLocation mapLocation = new MapLocation();
            mapLocation.populateFromParcel(parcel);
            return mapLocation;
        }

        @Override
        public MapLocation[] newArray(int i)
        {
            return new MapLocation[i];
        }
    };
}
