package com.michaldabski.panoramiotest.photo_activity;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Michal on 10/08/2014.
 *
 * http://developer.android.com/training/animation/screen-slide.html
 */
public class PhotoPageTransformer implements ViewPager.PageTransformer
{
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position)
    {
        int pageWidth = view.getWidth();

        if (position < -1)
        {
            view.setAlpha(0);

        } else if (position <= 0)
        {
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);

        } else if (position <= 1)
        {
            view.setAlpha(1 - position);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else
        {
            view.setAlpha(0);
        }
    }
}
