package com.michaldabski.panoramio.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Michal on 10/08/2014.
 */
public class VolleySingleton
{
    private static VolleySingleton instance;
    private final Context context;

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private LruImageCache imageCache;

    private VolleySingleton(Context context)
    {
        // in case activity is passed, get application context
        // to prevent memory leak by referencing activity in a singleton.
        this.context = context.getApplicationContext();
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
    }

    public LruImageCache getImageCache()
    {
        if (imageCache == null)
            imageCache = new LruImageCache();
        return imageCache;
    }

    public ImageLoader getImageLoader()
    {
        if (imageLoader == null)
            imageLoader = new ImageLoader(getRequestQueue(), getImageCache());
        return imageLoader;
    }

    public static VolleySingleton getInstance(Context context)
    {
        if (instance == null)
            instance = new VolleySingleton(context);
        return instance;
    }
}
