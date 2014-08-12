package com.michaldabski.panoramio;

import android.app.Application;

import com.michaldabski.panoramio.utils.VolleySingleton;

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
