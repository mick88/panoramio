package com.michaldabski.panoramiotest.wallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.michaldabski.panoramiotest.R;

/**
 * Created by Michal on 11/08/2014.
 */
public class NearbyPhotoWallpaperService extends WallpaperService implements Response.ErrorListener
{
    BitmapDrawable bitmap;
    @Override
    public Engine onCreateEngine()
    {
        return new NearbyPhotoWallpaperEngine();
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {

    }

    private class NearbyPhotoWallpaperEngine extends Engine
    {
        void draw()
        {
            Canvas canvas = getSurfaceHolder().lockCanvas();
            bitmap.draw(canvas);
            getSurfaceHolder().unlockCanvasAndPost(canvas);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder)
        {
            super.onSurfaceCreated(holder);
            draw();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            super.onSurfaceChanged(holder, format, width, height);
            draw();
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            super.onVisibilityChanged(visible);
            draw();
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        this.bitmap = new BitmapDrawable(getResources(), bitmap);
        /*RequestQueue requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();

        requestQueue.add(new NearbyPhotosRequest(this, 0f, 0f, 0, 100));*/
    }
}
