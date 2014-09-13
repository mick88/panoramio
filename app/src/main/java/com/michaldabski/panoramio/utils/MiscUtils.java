package com.michaldabski.panoramio.utils;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Michal on 13-Sep-14.
 */
public class MiscUtils
{
    public static boolean isGooglePlayAvailable(Context context)
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return status == ConnectionResult.SUCCESS;
    }
}
