package com.michaldabski.panoramio.photo_activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michaldabski.panoramio.R;
import com.michaldabski.panoramio.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends Activity implements ViewPager.OnPageChangeListener
{
    public static final String
            ARG_PHOTOS_ARRAY = "photos",
            ARG_SELECTED_INDEX = "selected_photo";
    private static final String
        STATE_MAP_VISIBILITY = "map_visibility";
    private static final float MAP_ZOOM_LEVEL = 15f;
    ViewPager viewPager;
    List<Photo> photos;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Bundle extras = getIntent().getExtras();
        Parcelable[] parcelableArray = extras.getParcelableArray(ARG_PHOTOS_ARRAY);

        photos = new ArrayList<Photo>(parcelableArray.length);
        for (Parcelable parcelable : parcelableArray)
            photos.add((Photo) parcelable);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageTransformer(true, new PhotoPageTransformer());
        viewPager.setAdapter(new PhotoPagerAdapter(getFragmentManager(), photos));
        viewPager.setOnPageChangeListener(this);
        // if activity just launched, show photo selected by user
        if (savedInstanceState == null)
        {
            viewPager.setCurrentItem(extras.getInt(ARG_SELECTED_INDEX));
            findViewById(R.id.fragmentMap).setVisibility(View.GONE);
        }
        else
        {
            int visibility = savedInstanceState.getInt(STATE_MAP_VISIBILITY, View.GONE);
            //noinspection ResourceType
            findViewById(R.id.fragmentMap).setVisibility(visibility);
        }

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
        setupMap(mapFragment.getMap());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.photos, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_MAP_VISIBILITY, findViewById(R.id.fragmentMap).getVisibility());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.actionLocation:
                View view = findViewById(R.id.fragmentMap);
                if (view.getVisibility() == View.VISIBLE)
                {
                    view.setVisibility(View.GONE);
                    mapFragment.setUserVisibleHint(false);
                }
                else
                {
                    view.setVisibility(View.VISIBLE);
                    mapFragment.setUserVisibleHint(true);
                    int position = viewPager.getCurrentItem();
                    showPhotoLocation(photos.get(position), false);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2)
    {

    }

    void setupMap(GoogleMap googleMap)
    {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        for (Photo photo : photos)
        {
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(photo.getLatitude(), photo.getLongitude())).title(photo.getPhotoTitle());
            googleMap.addMarker(markerOptions);
        }
    }

    void showPhotoLocation(Photo photo, boolean animate)
    {
        if (mapFragment == null) return;
        GoogleMap map = mapFragment.getMap();
        if (map != null)
        {
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(photo.getLatitude(), photo.getLongitude()), MAP_ZOOM_LEVEL);
            if (animate) map.animateCamera(cameraUpdate);
            else map.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onPageSelected(int position)
    {
        showPhotoLocation(photos.get(position), true);
    }

    @Override
    public void onPageScrollStateChanged(int i)
    {

    }
}
