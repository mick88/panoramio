package com.michaldabski.panoramio.photo_providers;

/**
 * Created by Michal on 21/09/2014.
 */
public interface PhotoProvider
{
    void getImages(int from, int to, PhotoListener photoListener);
    boolean isLoading();
}
