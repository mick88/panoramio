package com.michaldabski.panoramio.main_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.michaldabski.panoramio.R;
import com.michaldabski.panoramio.models.Photo;
import com.michaldabski.panoramio.utils.VolleySingleton;

import java.util.List;

/**
 * Created by Michal on 10/08/2014.
 */
public class PhotoGridAdapter extends ArrayAdapter<Photo>
{
    private final ImageLoader imageLoader;

    public PhotoGridAdapter(Context context, List<Photo> photos)
    {
        super(context, 0, photos);
        imageLoader = VolleySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        NetworkImageView imageView = (NetworkImageView) convertView;
        if (imageView == null)
            imageView = (NetworkImageView) LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);

        Photo photo = getItem(position);
        imageView.setImageUrl(photo.getUrl(), imageLoader);

        return imageView;
    }
}
