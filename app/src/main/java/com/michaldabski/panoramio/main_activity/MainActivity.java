package com.michaldabski.panoramio.main_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.michaldabski.panoramio.R;
import com.michaldabski.panoramio.models.PanoramioResponse;
import com.michaldabski.panoramio.models.Photo;
import com.michaldabski.panoramio.photo_activity.PhotoActivity;
import com.michaldabski.panoramio.requests.NearbyPhotosRequest;
import com.michaldabski.panoramio.requests.PanoramioRequest;
import com.michaldabski.panoramio.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class MainActivity extends Activity implements Response.ErrorListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener
{
    private static final String
            STATE_RESPONSE = "response",
            STATE_LAT = "latitude",
            STATE_LONG = "longitude";
    private ArrayList<Photo> photos = new ArrayList<Photo>();

    PanoramioResponse panoramioResponse;
    PanoramioRequest panoramioRequest = null;
    float latitude=Float.NaN,
            longitude=Float.NaN;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
        {
            panoramioResponse = savedInstanceState.getParcelable(STATE_RESPONSE);
            latitude = savedInstanceState.getFloat(STATE_LAT);
            longitude = savedInstanceState.getFloat(STATE_LONG);
        }

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(new PhotoGridAdapter(this, photos));
        gridView.setOnScrollListener(this);

        if (panoramioResponse == null)
            acquireLocation();
        else
        {
            addPhotos(panoramioResponse.getPhotos());
            panoramioResponse.setPhotos(this.photos);
        }

    }

    void addPhotos(Collection<Photo> photoCollection)
    {
        photos.addAll(photoCollection);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        PhotoGridAdapter adapter = (PhotoGridAdapter) gridView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    void onPanoramioResponse(PanoramioResponse response)
    {
        this.panoramioResponse = response;
        addPhotos(panoramioResponse.getPhotos());
        panoramioResponse.setPhotos(this.photos);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RESPONSE, panoramioResponse);
        outState.putFloat(STATE_LAT, latitude);
        outState.putFloat(STATE_LONG, longitude);
    }

    void acquireLocation()
    {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setProgressBarIndeterminateVisibility(true);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                requestPhotos(latitude, longitude);
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
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider != null && locationManager.isProviderEnabled(bestProvider))
        {
            locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
        }
        else
        {
            Random random = new Random(System.currentTimeMillis());
            Location location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setAccuracy(1f);
            location.setLatitude((random.nextDouble() * 180d) - 90d);
            location.setLongitude((random.nextDouble() * 360d) - 180d);
            locationListener.onLocationChanged(location);
            Toast.makeText(this, "No location provider. Using random location.", Toast.LENGTH_SHORT).show();
        }
    }

    void requestPhotos(float lat, float lng)
    {
        requestPhotos(lat, lng, 0);
    }

    void requestPhotos(float lat, float lng, int from)
    {
        setProgressBarIndeterminateVisibility(true);
        panoramioRequest = new NearbyPhotosRequest(this, lat, lng, from, 0.1f)
        {
            @Override
            protected void deliverResponse(PanoramioResponse response)
            {
                super.deliverResponse(response);
                onPanoramioResponse(response);
                panoramioRequest = null;
                setProgressBarIndeterminateVisibility(false);
            }
        };
        panoramioRequest.setTag(this);
        VolleySingleton.getInstance(this).getRequestQueue().add(panoramioRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        panoramioRequest = null;
        setProgressBarIndeterminateVisibility(false);
        error.printStackTrace();
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    boolean isRequestPending()
    {
        return panoramioRequest != null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoActivity.ARG_PHOTOS_ARRAY, panoramioResponse.getPhotos().toArray(new Photo[panoramioResponse.getPhotos().size()]));
        intent.putExtra(PhotoActivity.ARG_SELECTED_INDEX, position);

        startActivity(intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState)
    {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (firstVisibleItem != 0 && totalItemCount <= firstVisibleItem+visibleItemCount && !isRequestPending() && Float.isNaN(latitude) == false && totalItemCount < panoramioResponse.getCount())
        {
            requestPhotos(latitude, longitude, photos.size());
        }
    }
}
