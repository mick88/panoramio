package com.michaldabski.panoramiotest.photo_activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.michaldabski.panoramiotest.R;

public class PhotoActivity extends Activity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageTransformer(false, new PhotoPageTransformer());
    }
}
