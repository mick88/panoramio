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

import com.michaldabski.panoramio.R;
import com.michaldabski.panoramio.about.AboutActivity;
import com.michaldabski.panoramio.models.Photo;
import com.michaldabski.panoramio.photo_activity.PhotoActivity;
import com.michaldabski.panoramio.requests.NearbyPhotosRequest;
import com.michaldabski.panoramio.utils.AddressResolver;
import com.michaldabski.panoramio.utils.VolleySingleton;
import com.michaldabski.panoramio.photo_providers.PanoramioPhotoProvider;
import com.michaldabski.panoramio.photo_providers.PhotoListener;
import com.michaldabski.panoramio.photo_providers.PhotoProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements PhotoListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener
{
    private static final String
            STATE_HAS_MORE = "has_more",
            STATE_PHOTOS = "photos",
            STATE_ADDRESS = "address",
            STATE_LAT = "latitude",
            STATE_LONG = "longitude";
    private String address;
    private ArrayList<Photo> photos = null;
    private PhotoProvider photoProvider=null;
    boolean hasMoreImages=false;

    float latitude=Float.NaN,
            longitude=Float.NaN;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
        {
            photos = savedInstanceState.getParcelableArrayList(STATE_PHOTOS);
            latitude = savedInstanceState.getFloat(STATE_LAT);
            longitude = savedInstanceState.getFloat(STATE_LONG);
            address = savedInstanceState.getString(STATE_ADDRESS);
            hasMoreImages = savedInstanceState.getBoolean(STATE_HAS_MORE);

            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.setSubtitle(address);
        }

        if (photos == null)
        {
            photos = new ArrayList<Photo>(NearbyPhotosRequest.NUM_PHOTOS);
            acquireLocation();
        }

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(new PhotoGridAdapter(this, photos));
        gridView.setOnScrollListener(this);
    }

    public PhotoProvider getPhotoProvider()
    {
        if (photoProvider == null)
            photoProvider = new PanoramioPhotoProvider(latitude, longitude, VolleySingleton.getInstance(this).getRequestQueue(), this);
        return photoProvider;
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_PHOTOS, photos);
        outState.putFloat(STATE_LAT, latitude);
        outState.putFloat(STATE_LONG, longitude);
        outState.putString(STATE_ADDRESS, address);
        outState.putBoolean(STATE_HAS_MORE, hasMoreImages);
    }

    void acquireLocation()
    {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        findViewById(R.id.progressContainer).setVisibility(View.VISIBLE);
        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                requestPhotos();
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
            Toast.makeText(this, R.string.no_location_provider, Toast.LENGTH_SHORT).show();
        }
    }

    void requestPhotos()
    {
        TextView tvProgress = (TextView) findViewById(R.id.tvProgress);
        tvProgress.setText(R.string.progress_photos);
        requestPhotos(0);
    }

    void requestPhotos(int from)
    {
        getPhotoProvider().getImages(from, from + NearbyPhotosRequest.NUM_PHOTOS, this);
    }

    boolean isRequestPending()
    {
        return photoProvider != null && photoProvider.isLoading();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra(PhotoActivity.ARG_PHOTOS_ARRAY, photos.toArray(new Photo[photos.size()]));
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
        if (firstVisibleItem != 0
                && hasMoreImages
                && totalItemCount <= firstVisibleItem+visibleItemCount
                && !isRequestPending()
                && Float.isNaN(latitude) == false
                )
        {
            requestPhotos(photos.size());
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

    @Override
    public void onPhotosLoaded(List<Photo> photos, boolean hasMore)
    {
        addPhotos(photos);
        findViewById(R.id.progressContainer).setVisibility(View.GONE);
        this.hasMoreImages=hasMore;
    }

    @Override
    public void onError(Exception error)
    {
        error.printStackTrace();
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .show();
        findViewById(R.id.progressContainer).setVisibility(View.GONE);
    }
}
