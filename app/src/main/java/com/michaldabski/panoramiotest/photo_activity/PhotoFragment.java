package com.michaldabski.panoramiotest.photo_activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.michaldabski.panoramiotest.R;
import com.michaldabski.panoramiotest.models.Photo;

/**
 * Created by Michal on 10/08/2014.
 */
public class PhotoFragment extends Fragment
{
    public static final String ARG_PHOTO = "photo";
    Photo photo;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        photo = getArguments().getParcelable(ARG_PHOTO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        PhotoActivity activity = (PhotoActivity) getActivity();
        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.imgImage);
        imageView.setImageUrl(photo.getUrl(), activity.getImageLoader());

        TextView tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
        tvAuthor.setText(photo.getOwnerName());
    }

    public static PhotoFragment newInstance(Photo photo)
    {
        Bundle args = new Bundle(1);
        args.putParcelable(ARG_PHOTO, photo);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
