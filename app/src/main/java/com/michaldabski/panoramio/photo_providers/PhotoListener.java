package com.michaldabski.panoramio.photo_providers;

import com.michaldabski.panoramio.models.Photo;

import java.util.List;

/**
 * Created by Michal on 21/09/2014.
 */
public interface PhotoListener
{
    void onPhotosLoaded(List<Photo> photos, boolean hasMore);
    void onError(Exception error);
}
