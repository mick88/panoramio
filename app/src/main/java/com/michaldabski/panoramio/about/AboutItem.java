package com.michaldabski.panoramio.about;

import android.app.Activity;
import android.widget.TextView;

/**
 * Created by Michal on 14-Sep-14.
 */
public interface AboutItem
{
    void setText(TextView textView);
    void onClicked(Activity activity);
}
