package com.michaldabski.panoramiotest.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Created by Michal on 08/08/2014.
 */
public class GsonRequest<T> extends Request<T>
{
    private final Class<T> type;
    public GsonRequest(int method, String url, Response.ErrorListener listener, Class<T> type)
    {
        super(method, url, listener);
        this.type = type;
    }

    protected static String getResponseString(NetworkResponse networkResponse) throws UnsupportedEncodingException
    {
        return new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse)
    {
        try
        {
            T response = new Gson().fromJson(getResponseString(networkResponse), type);
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(networkResponse));
        }
        catch (Exception e)
        {
            return Response.error(new VolleyError(e));
        }
    }

    @Override
    protected void deliverResponse(T response)
    {

    }
}
