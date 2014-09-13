package com.michaldabski.panoramio.photo_activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michaldabski.panoramio.R;
import com.michaldabski.panoramio.models.Photo;
import com.michaldabski.panoramio.utils.MiscUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoActivity extends Activity implements ViewPager.OnPageChangeListener, GoogleMap.OnMarkerClickListener, View.OnSystemUiVisibilityChangeListener
{
    public static final String
            ARG_PHOTOS_ARRAY = "photos",
            ARG_SELECTED_INDEX = "selected_photo";
    private static final String
        STATE_FULLSCREEN = "fullscreen",
        STATE_MAP_VISIBILITY = "map_visibility";
    private static final float MAP_ZOOM_LEVEL = 15f;
    ViewPager viewPager;
    List<Photo> photos;
    MapFragment mapFragment;
    Map<Marker, Photo> markerPhotoMap;
    private boolean fullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setupActionbar(getActionBar());

        Bundle extras = getIntent().getExtras();
        Parcelable[] parcelableArray = extras.getParcelableArray(ARG_PHOTOS_ARRAY);

        markerPhotoMap = new HashMap<Marker, Photo>(parcelableArray.length);
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
            if (savedInstanceState.getBoolean(STATE_FULLSCREEN))
                enableFullscreen();
        }

        setPhotoTitleAndAuthor(getActionBar(), photos.get(viewPager.getCurrentItem()));

        if (MiscUtils.isGooglePlayAvailable(this))
        {
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
            if (mapFragment.getMap() != null)
                setupMap(mapFragment.getMap());
        }
    }

    void setupActionbar(ActionBar actionBar)
    {
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.photos, menu);

        if (MiscUtils.isGooglePlayAvailable(this) == false)
        {
            menu.findItem(R.id.actionLocation).setVisible(false);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_MAP_VISIBILITY, findViewById(R.id.fragmentMap).getVisibility());
        outState.putBoolean(STATE_FULLSCREEN, fullscreen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
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
    public void onBackPressed()
    {
        if (findViewById(R.id.fragmentMap).getVisibility() == View.VISIBLE)
            findViewById(R.id.fragmentMap).setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2)
    {

    }

    void setupMap(GoogleMap googleMap)
    {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setOnMarkerClickListener(this);
        for (Photo photo : photos)
        {
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(photo.getLatitude(), photo.getLongitude())).title(photo.getPhotoTitle());
            Marker marker = googleMap.addMarker(markerOptions);
            markerPhotoMap.put(marker, photo);
        }
    }

    void showPhotoLocation(Photo photo, boolean animate)
    {
        if (mapFragment == null) return;
        GoogleMap map = mapFragment.getMap();
        if (map != null && findViewById(R.id.fragmentMap).getVisibility() == View.VISIBLE)
        {
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(photo.getLatitude(), photo.getLongitude()), MAP_ZOOM_LEVEL);
            if (animate) map.animateCamera(cameraUpdate);
            else map.moveCamera(cameraUpdate);

            for (Map.Entry<Marker, Photo> entry : markerPhotoMap.entrySet())
                if (entry.getValue().equals(photo))
                    entry.getKey().showInfoWindow();
        }
    }

    @Override
    public void onPageSelected(int position)
    {
        showPhotoLocation(photos.get(position), true);
        setPhotoTitleAndAuthor(getActionBar(), photos.get(position));
    }

    void setPhotoTitleAndAuthor(ActionBar actionBar, Photo photo)
    {
        if (actionBar == null) return;

        actionBar.setTitle(photo.getPhotoTitle());
        actionBar.setSubtitle(getString(R.string.by_s, photo.getOwnerName()));
    }

    @Override
    public void onPageScrollStateChanged(int i)
    {

    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        Photo photo = markerPhotoMap.get(marker);
        if (photo != null)
        {
            int position = photos.indexOf(photo);
            viewPager.setCurrentItem(position, true);
            return true;
        }
        else return false;
    }

    public void disableFullScreen()
    {
        fullscreen = false;

        findViewById(R.id.viewPager).setSystemUiVisibility(
                0
        );
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void enableFullscreen()
    {
        fullscreen = true;
        View view = findViewById(R.id.viewPager);
        view.setOnSystemUiVisibilityChangeListener(this);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public boolean isFullscreen()
    {
        return fullscreen;
    }

    public boolean toggleFullscreen()
    {
        if (isFullscreen())
            disableFullScreen();
        else enableFullscreen();
        return isFullscreen();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility)
    {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
            fullscreen = false;
    }
}
