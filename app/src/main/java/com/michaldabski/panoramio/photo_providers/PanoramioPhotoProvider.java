package com.michaldabski.panoramio.photo_providers;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.michaldabski.panoramio.models.PanoramioResponse;
import com.michaldabski.panoramio.requests.NearbyPhotosRequest;


/**
 * Created by Michal on 21/09/2014.
 */
public class PanoramioPhotoProvider implements PhotoProvider
{
    private static final int DISTANCE_MULTIPLIER = 3;
    boolean loading;
    final float lat, lng;
    float distance=0.1f;
    final RequestQueue requestQueue;
    final Object tag;

    public PanoramioPhotoProvider(float lat, float lng, RequestQueue requestQueue, Object tag)
    {
        this.lat = lat;
        this.lng = lng;
        this.requestQueue = requestQueue;
        this.tag = tag;
    }

    @Override
    public void getImages(final int from, final int to, final PhotoListener photoListener)
    {
        this.loading = true;
        Request<?> request = new NearbyPhotosRequest(new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                loading = false;
                photoListener.onError(error);
            }
        }, lat, lng, from, to, distance)
        {
            @Override
            protected void deliverResponse(PanoramioResponse response)
            {
                super.deliverResponse(response);
                if (response.isEmpty())
                {
                    distance *= DISTANCE_MULTIPLIER;
                    getImages(from, to, photoListener);
                }
                else
                {
                    loading = false;
                    photoListener.onPhotosLoaded(response.getPhotos(), response.isHasMore());
                }
            }
        };
        request.setTag(this.tag);
        requestQueue.add(request);
    }

    @Override
    public boolean isLoading()
    {
        return loading;
    }
}
