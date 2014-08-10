package com.michaldabski.panoramiotest.photo_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.michaldabski.panoramiotest.LruImageCache;
import com.michaldabski.panoramiotest.R;
import com.michaldabski.panoramiotest.models.PanoramioResponse;
import com.michaldabski.panoramiotest.requests.PanoramioRequest;

import java.util.Random;

public class PhotoActivity extends Activity implements Response.ErrorListener
{
    private static final String STATE_RESPONSE = "response";
    static final int NUM_PHOTOS = 50;
    private final Random random = new Random(System.currentTimeMillis());

    PanoramioResponse panoramioResponse;

    RequestQueue requestQueue;
    ImageLoader imageLoader;
    LruImageCache imageCache;

    float minx = -180,
            miny = -90,
            maxx = 180,
            maxy = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        imageCache = new LruImageCache();
        imageLoader = new ImageLoader(requestQueue, imageCache);

        if (savedInstanceState != null)
        {
            panoramioResponse = savedInstanceState.getParcelable(STATE_RESPONSE);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageTransformer(false, new PhotoPageTransformer());

        if (panoramioResponse == null)
            acquireLocation();
        else
        {
            viewPager.setAdapter(new PhotoPagerAdapter(getFragmentManager(), panoramioResponse.getPhotos()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RESPONSE, panoramioResponse);
    }

    void acquireLocation()
    {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                minx = (float) (location.getLongitude() - 1f);
                miny = (float) (location.getLatitude() -1f);
                maxx = (float) (location.getLongitude() + 1f);
                maxy = (float) (location.getLatitude() + 1f);

                Log.d("Location", location.toString());
                Log.d("Location", String.format("%f %f %f %f", miny, minx, maxy, maxx));
                requestPhotos();
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {

            }

            @Override
            public void onProviderEnabled(String s)
            {

            }

            @Override
            public void onProviderDisabled(String s)
            {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    void requestPhotos()
    {
        int from = random.nextInt(1000);
        requestPhotos(from, from+NUM_PHOTOS);
    }

    void requestPhotos(int from, int to)
    {
        PanoramioRequest panoramioRequest = new PanoramioRequest(this, minx, miny, maxx, maxy, from, to)
        {
            @Override
            protected void deliverResponse(PanoramioResponse response)
            {
                super.deliverResponse(response);
                onPanoramioResponse(response);
            }
        };
        requestQueue.add(panoramioRequest);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        requestQueue.stop();
    }

    public ImageLoader getImageLoader()
    {
        return imageLoader;
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        error.printStackTrace();
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    void onPanoramioResponse(PanoramioResponse response)
    {
        Log.d("Response", response.toString());
        this.panoramioResponse = response;
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new PhotoPagerAdapter(getFragmentManager(), response.getPhotos()));
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        imageCache.clear();
    }
}
