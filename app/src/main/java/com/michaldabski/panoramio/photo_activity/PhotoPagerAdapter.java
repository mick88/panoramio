package com.michaldabski.panoramio.photo_activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.michaldabski.panoramio.models.Photo;

import java.util.List;

/**
 * Created by Michal on 10/08/2014.
 */
public class PhotoPagerAdapter extends FragmentStatePagerAdapter
{
    private final List<Photo> photos;

    public PhotoPagerAdapter(FragmentManager fm, List<Photo> photos)
    {
        super(fm);
        this.photos = photos;
    }

    @Override
    public Fragment getItem(int pos)
    {
        return PhotoFragment.newInstance(photos.get(pos));
    }

    @Override
    public int getCount()
    {
        return photos.size();
    }
}
