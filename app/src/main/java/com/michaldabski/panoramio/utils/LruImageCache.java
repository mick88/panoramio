package com.michaldabski.panoramio.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Michal on 08/08/2014.
 */
public class LruImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache
{
    public LruImageCache()
    {
        super(getOptimalCacheSize());
    }

    @Override
    public Bitmap getBitmap(String url)
    {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap)
    {
        put(url, bitmap);
    }

    public void clear()
    {
        evictAll();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected int sizeOf(String key, Bitmap value)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return value.getByteCount();
        else
            return value.getAllocationByteCount();
    }

    public static int getOptimalCacheSize()
    {
        long maxMemory = Runtime.getRuntime().maxMemory();
        return (int) (0.4f * maxMemory);
    }

}
