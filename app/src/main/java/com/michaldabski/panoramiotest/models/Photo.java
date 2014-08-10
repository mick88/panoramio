package com.michaldabski.panoramiotest.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Michal on 08/08/2014.
 */
public class Photo
{
    int height;
    int width;
    float latitude;
    float longitude;
    @SerializedName("owner_id")
    int ownerId;
    @SerializedName("owner_name")
    String ownerName;
    @SerializedName("owner_url")
    String ownerUrl;
    @SerializedName("photo_file_url")
    String url;
    @SerializedName("photo_id")
    int photoId;
    @SerializedName("photo_title")
    String photoTitle;
    @SerializedName("upload_date")
    String uploadDate;

    public String getUrl()
    {
        return url;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (photoId != photo.photoId) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return photoId;
    }

    public int getHeight()
    {

        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public float getLatitude()
    {
        return latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

    public int getOwnerId()
    {
        return ownerId;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public String getOwnerUrl()
    {
        return ownerUrl;
    }

    public int getPhotoId()
    {
        return photoId;
    }

    public String getPhotoTitle()
    {
        return photoTitle;
    }

    public String getUploadDate()
    {
        return uploadDate;
    }

    @Override
    public String toString()
    {
        return "Photo{" +
                "height=" + height +
                ", width=" + width +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", ownerId=" + ownerId +
                ", ownerName='" + ownerName + '\'' +
                ", ownerUrl='" + ownerUrl + '\'' +
                ", url='" + url + '\'' +
                ", photoId=" + photoId +
                ", photoTitle='" + photoTitle + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                '}';
    }
}
