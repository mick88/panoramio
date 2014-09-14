package com.michaldabski.panoramio.main_activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.michaldabski.panoramio.R;
import com.michaldabski.panoramio.about.AboutActivity;
import com.michaldabski.panoramio.models.PanoramioResponse;
import com.michaldabski.panoramio.models.Photo;
import com.michaldabski.panoramio.photo_activity.PhotoActivity;
import com.michaldabski.panoramio.requests.NearbyPhotosRequest;
import com.michaldabski.panoramio.requests.PanoramioRequest;
import com.michaldabski.panoramio.utils.AddressResolver;
import com.michaldabski.panoramio.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class MainActivity extends Activity implements Response.ErrorListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener
{
    private static final String
            STATE_RESPONSE = "response",
            STATE_DISTANCE = "distance",
            STATE_ADDRESS = "address",
            STATE_LAT = "latitude",
            STATE_LONG = "longitude";
    private float distance = 0.1f;
    private String address;
    private ArrayList<Photo> photos = new ArrayList<Photo>();

    PanoramioResponse panoramioResponse;
    PanoramioRequest panoramioRequest = null;
    float latitude=Float.NaN,
            longitude=Float.NaN;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
        {
            panoramioResponse = savedInstanceState.getParcelable(STATE_RESPONSE);
            latitude = savedInstanceState.getFloat(STATE_LAT);
            longitude = savedInstanceState.getFloat(STATE_LONG);
            distance = savedInstanceState.getFloat(STATE_DISTANCE);
            address = savedInstanceState.getString(STATE_ADDRESS);

            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.setSubtitle(address);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    void addPhotos(Collection<Photo> photoCollection)
    {
        photos.addAll(photoCollection);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        PhotoGridAdapter adapter = (PhotoGridAdapter) gridView.getAdapter();
        adapter.notifyDataSetChanged();
        findViewById(R.id.progressContainer).setVisibility(View.GONE);
    }

    void onPanoramioResponse(PanoramioResponse response)
    {
        if (response.isEmpty())
        {
            // if no photos received, increase area and try again
            distance *= 3;
            requestPhotos(latitude, longitude);
        }
        else
        {
            this.panoramioResponse = response;
            addPhotos(panoramioResponse.getPhotos());
            panoramioResponse.setPhotos(this.photos);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RESPONSE, panoramioResponse);
        outState.putFloat(STATE_LAT, latitude);
        outState.putFloat(STATE_LONG, longitude);
        outState.putFloat(STATE_DISTANCE, distance);
        outState.putString(STATE_ADDRESS, address);
    }

    void acquireLocation()
    {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                requestPhotos(latitude, longitude);
                locationManager.removeUpdates(this);

                new AddressResolver(getApplicationContext())
                {
                    @Override
                    protected void onPostExecute(String address)
                    {
                        if (address != null && isFinishing() == false)
                        {
                            MainActivity.this.address = address;
                            ActionBar actionBar = getActionBar();
                            if (actionBar != null)
                                actionBar.setSubtitle(address);
                        }
                    }
                }.execute(location.getLatitude(), location.getLongitude());
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
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
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
        TextView tvProgress = (TextView) findViewById(R.id.tvProgress);
        tvProgress.setText(R.string.progress_photos);
        requestPhotos(lat, lng, 0);
    }

    void requestPhotos(float lat, float lng, int from)
    {
        panoramioRequest = new NearbyPhotosRequest(this, lat, lng, from, distance)
        {
            @Override
            protected void deliverResponse(PanoramioResponse response)
            {
                super.deliverResponse(response);
                onPanoramioResponse(response);
                panoramioRequest = null;
            }
        };
        panoramioRequest.setTag(this);
        VolleySingleton.getInstance(this).getRequestQueue().add(panoramioRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        panoramioRequest = null;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.actionAbout:
                Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
