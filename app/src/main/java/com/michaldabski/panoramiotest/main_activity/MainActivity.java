package com.michaldabski.panoramiotest.main_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.michaldabski.panoramiotest.R;
import com.michaldabski.panoramiotest.models.PanoramioResponse;
import com.michaldabski.panoramiotest.models.Photo;
import com.michaldabski.panoramiotest.photo_activity.PhotoActivity;
import com.michaldabski.panoramiotest.requests.NearbyPhotosRequest;
import com.michaldabski.panoramiotest.requests.PanoramioRequest;
import com.michaldabski.panoramiotest.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends Activity implements Response.ErrorListener, AdapterView.OnItemClickListener
{
    private static final String STATE_RESPONSE = "response";
    private List<Photo> photos = new ArrayList<Photo>();

    PanoramioResponse panoramioResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
        {
            panoramioResponse = savedInstanceState.getParcelable(STATE_RESPONSE);
        }

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(new PhotoGridAdapter(this, photos));

        if (panoramioResponse == null)
            acquireLocation();
        else
        {
            addPhotos(panoramioResponse.getPhotos());
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
                requestPhotos((float)location.getLatitude(), (float)location.getLongitude());
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

    void requestPhotos(float lat, float lng)
    {
        requestPhotos(lat, lng, 0, 100);
    }

    void requestPhotos(float lat, float lng, int from, int to)
    {
        PanoramioRequest panoramioRequest = new NearbyPhotosRequest(this, lat, lng, from, to)
        {
            @Override
            protected void deliverResponse(PanoramioResponse response)
            {
                super.deliverResponse(response);
                onPanoramioResponse(response);
            }
        };
        panoramioRequest.setTag(this);
        VolleySingleton.getInstance(this).getRequestQueue().add(panoramioRequest);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoActivity.ARG_PHOTOS_ARRAY, panoramioResponse.getPhotos().toArray(new Photo[panoramioResponse.getPhotos().size()]));
        intent.putExtra(PhotoActivity.ARG_SELECTED_INDEX, position);

        startActivity(intent);
    }
}
