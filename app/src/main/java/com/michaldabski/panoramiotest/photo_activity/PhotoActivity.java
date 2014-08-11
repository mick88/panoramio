package com.michaldabski.panoramiotest.photo_activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;

import com.michaldabski.panoramiotest.R;
import com.michaldabski.panoramiotest.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends Activity
{
    public static final String
            ARG_PHOTOS_ARRAY = "photos",
            ARG_SELECTED_INDEX = "selected_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Bundle extras = getIntent().getExtras();
        Parcelable[] parcelableArray = extras.getParcelableArray(ARG_PHOTOS_ARRAY);
        List<Photo> photos = new ArrayList<Photo>(parcelableArray.length);
        for (Parcelable parcelable : parcelableArray)
            photos.add((Photo) parcelable);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageTransformer(true, new PhotoPageTransformer());
        viewPager.setAdapter(new PhotoPagerAdapter(getFragmentManager(), photos));
        // if activity just launched, show photo selected by user
        if (savedInstanceState == null)
            viewPager.setCurrentItem(extras.getInt(ARG_SELECTED_INDEX));
    }
}
