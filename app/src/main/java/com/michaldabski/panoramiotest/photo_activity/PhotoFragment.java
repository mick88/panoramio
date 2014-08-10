package com.michaldabski.panoramiotest.photo_activity;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.michaldabski.panoramiotest.R;
import com.michaldabski.panoramiotest.models.Photo;
import com.michaldabski.panoramiotest.utils.VolleySingleton;

import java.util.Locale;

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
        setHasOptionsMenu(true);
        photo = getArguments().getParcelable(ARG_PHOTO);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.photo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.actionLocation:
                final String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%f,%f(%s)",
                        photo.getLatitude(), photo.getLongitude(), photo.getPhotoTitle());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;

            case R.id.actionDetails:
                PhotoDetailsDialogFragment.newInstance(photo)
                        .show(getFragmentManager(), PhotoDetailsDialogFragment.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        imageView.setImageUrl(photo.getUrl(), VolleySingleton.getInstance(getActivity()).getImageLoader());

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
