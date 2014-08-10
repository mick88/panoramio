package com.michaldabski.panoramiotest;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.michaldabski.panoramiotest.models.PanoramioResponse;
import com.michaldabski.panoramiotest.models.Photo;
import com.michaldabski.panoramiotest.requests.PanoramioRequest;

import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements Response.ErrorListener, View.OnClickListener
{
    private static final String STATE_CURRENT_IMAGE = "current_image";
    static final int NUM_PHOTOS = 50;
    private final Random random = new Random(System.currentTimeMillis());

    PanoramioResponse panoramioResponse;
    String currentImage;

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

        acquireLocation();

        requestPhotos();

        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.imgImage);
        imageView.setOnClickListener(this);
        if (savedInstanceState != null)
        {
            currentImage = savedInstanceState.getString(STATE_CURRENT_IMAGE);
            imageView.setImageUrl(currentImage, imageLoader);
        }
    }

    void acquireLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        if (currentImage == null)
            setRandomPhoto();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (currentImage != null)
            outState.putString(STATE_CURRENT_IMAGE, currentImage);
    }

    void setRandomPhoto()
    {
        if (panoramioResponse != null)
        {
            List<Photo> photos = panoramioResponse.getPhotos();
            if (photos.isEmpty() == false)
            {
                int pos = random.nextInt(photos.size());
                setPhoto(photos.get(pos));
                currentImage = photos.get(pos).getUrl();
            }
        }
    }

    void setPhoto(Photo photo)
    {
        this.currentImage = photo.getUrl();
        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.imgImage);
        imageView.setImageUrl(currentImage, imageLoader);

        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvAuthor.setText(photo.getOwnerName());

        Log.d("Photo set", photo.toString());
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        imageCache.clear();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imgImage:
                setRandomPhoto();
                break;
        }
    }
}
