package com.michaldabski.panoramiotest;

import com.michaldabski.panoramiotest.models.Photo;

import java.util.Comparator;

/**
 * Created by Michal on 10/08/2014.
 *
 * Comparator used to sort photos by proximity
 */
public class PhotoDistanceComparator implements Comparator<Photo>
{
    private final float userLat, userLong;

    public static double distance(float lat1, float lon1, float lat2, float lon2)
    {
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }


    private float distanceToUser(float lat, float lng)
    {
        float dist = (float) distance(userLat, userLong, lat, lng);
        return dist;
    }

    private Float getPhotoDistance(Photo photo)
    {
        return distanceToUser(photo.getLatitude(), photo.getLongitude());
    }

    public PhotoDistanceComparator(float userLat, float userLong)
    {
        this.userLat = userLat;
        this.userLong = userLong;
    }

    @Override
    public int compare(Photo photo, Photo photo2)
    {
        return getPhotoDistance(photo)
                .compareTo(getPhotoDistance(photo2));
    }
}
