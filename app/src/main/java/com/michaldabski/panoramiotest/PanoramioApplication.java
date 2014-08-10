package com.michaldabski.panoramiotest;

import android.app.Application;

import com.michaldabski.panoramiotest.utils.VolleySingleton;

/**
 * Created by Michal on 10/08/2014.
 */
public class PanoramioApplication extends Application
{
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        VolleySingleton.getInstance(this).getImageCache().clear();
    }
}
