package com.michaldabski.panoramio.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Michal on 08/08/2014.
 */
public class Photo implements Parcelable
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

    public Photo()
    {
    }

    public Photo(int height, int width, float latitude, float longitude, int ownerId, String ownerName, String ownerUrl, String url, int photoId, String photoTitle, String uploadDate)
    {
        this.height = height;
        this.width = width;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerUrl = ownerUrl;
        this.url = url;
        this.photoId = photoId;
        this.photoTitle = photoTitle;
        this.uploadDate = uploadDate;
    }

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeInt(height);
        parcel.writeInt(width);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeInt(ownerId);
        parcel.writeString(ownerName);
        parcel.writeString(ownerUrl);
        parcel.writeString(url);
        parcel.writeInt(photoId);
        parcel.writeString(photoTitle);
        parcel.writeString(uploadDate);
    }

    public void populateFromParcel(Parcel parcel)
    {
        height = parcel.readInt();
        width = parcel.readInt();
        latitude = parcel.readFloat();
        longitude = parcel.readFloat();
        ownerId = parcel.readInt();
        ownerName = parcel.readString();
        ownerUrl = parcel.readString();
        url = parcel.readString();
        photoId = parcel.readInt();
        photoTitle = parcel.readString();
        uploadDate = parcel.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>()
    {
        @Override
        public Photo createFromParcel(Parcel parcel)
        {
            Photo photo = new Photo();
            photo.populateFromParcel(parcel);
            return photo;
        }

        @Override
        public Photo[] newArray(int i)
        {
            return new Photo[i];
        }
    };
}
