package com.michaldabski.panoramiotest.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Michal on 08/08/2014.
 */
public class LruImageCache implements ImageLoader.ImageCache
{
    private final LruCache<String, Bitmap> bitmapLruCache = new LruCache<String, Bitmap>(20);

    @Override
    public Bitmap getBitmap(String url)
    {
        return bitmapLruCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap)
    {
        bitmapLruCache.put(url, bitmap);
    }

    public void clear()
    {
        bitmapLruCache.evictAll();
    }

}
