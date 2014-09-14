package com.michaldabski.panoramio.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal on 08/08/2014.
 */
public class PanoramioResponse implements Parcelable
{
    int count;
    @SerializedName("has_more")
    boolean hasMore;
    @SerializedName("map_location")
    MapLocation mapLocation;
    ArrayList<Photo> photos;

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

    public boolean isEmpty()
    {
        return photos.isEmpty();
    }

    public MapLocation getMapLocation()
    {
        return mapLocation;
    }

    public void setPhotos(ArrayList<Photo> photos)
    {
        this.photos = photos;
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(count);
        parcel.writeByte((byte) (hasMore ? 1 : 0));
        parcel.writeParcelable(mapLocation, flags);
        parcel.writeList(photos);
    }

    protected void populateFromParcel(Parcel parcel)
    {
        count = parcel.readInt();
        hasMore = parcel.readByte() == 1;
        mapLocation = parcel.readParcelable(MapLocation.class.getClassLoader());
        photos = parcel.readArrayList(Photo.class.getClassLoader());
    }

    public static final Creator<PanoramioResponse> CREATOR = new Creator<PanoramioResponse>()
    {
        @Override
        public PanoramioResponse createFromParcel(Parcel parcel)
        {
            PanoramioResponse response = new PanoramioResponse();
            response.populateFromParcel(parcel);
            return response;
        }

        @Override
        public PanoramioResponse[] newArray(int i)
        {
            return new PanoramioResponse[i];
        }
    };
}
