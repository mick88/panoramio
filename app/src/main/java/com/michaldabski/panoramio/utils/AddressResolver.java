package com.michaldabski.panoramio.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by Michal on 14-Sep-14.
 */
public class AddressResolver extends AsyncTask<Double, Void, String>
{
    private final Context context;

    public AddressResolver(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(Double... params)
    {
        final double
                lat = params[0],
                lng = params[1];

        try
        {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty()) return null;
            Address address = addresses.get(0);
            return buildAddressString(address.getAddressLine(0), address.getAddressLine(1), address.getPostalCode(), address.getAdminArea(), address.getLocality(), address.getCountryName());

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    static String buildAddressString(String... parts)
    {
        StringBuilder stringBuilder = new StringBuilder();
        LinkedHashSet<String> uniqueParts = new LinkedHashSet<String>(Arrays.asList(parts));

        boolean first = true;
        for (String part : uniqueParts)
            if (TextUtils.isEmpty(part) == false)
            {
                if (first)
                    first = false;
                else stringBuilder.append(", ");
                stringBuilder.append(part);
            }

        return stringBuilder.toString();
    }
}
