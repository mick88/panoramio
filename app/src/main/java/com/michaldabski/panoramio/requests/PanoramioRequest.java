package com.michaldabski.panoramio.requests;

import android.util.Log;

import com.android.volley.Response;
import com.michaldabski.panoramio.models.PanoramioResponse;

import java.util.Locale;

/**
 * Created by Michal on 08/08/2014.
 */
public class PanoramioRequest extends GsonRequest<PanoramioResponse>
{
    public static final String API_URL = "http://www.panoramio.com/map/get_panoramas.php?";
    private static final String URL_TEMPLATE = API_URL+"set=public&from=%d&to=%d&minx=%f&miny=%f&maxx=%f&maxy=%f&size=medium&mapfilter=true";

    public PanoramioRequest(Response.ErrorListener listener, float minx, float miny, float maxx, float maxy)
    {
        this(listener, minx, miny, maxx, maxy, 0, 20);
    }

    public PanoramioRequest(Response.ErrorListener listener, float minx, float miny, float maxx, float maxy, int from, int to)
    {
        super(Method.GET, buildUrl(minx, miny, maxx, maxy, from, to), listener, PanoramioResponse.class);
        Log.d("Request", getUrl());
    }

    private static String buildUrl(float minx, float miny, float maxx, float maxy, int from, int to)
    {
        if (Float.isNaN(minx) || Float.isNaN(miny) || Float.isNaN(maxx) || Float.isNaN(maxy))
            throw new IllegalArgumentException("NaN passed as coordinate");
        return String.format(Locale.ENGLISH, URL_TEMPLATE, from, to, minx, miny, maxx, maxy);
    }
}
