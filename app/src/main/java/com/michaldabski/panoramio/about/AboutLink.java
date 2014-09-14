package com.michaldabski.panoramio.about;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

/**
 * Created by Michal on 14-Sep-14.
 */
public class AboutLink implements AboutItem
{
    private final int drawableResource;
    private final int textResource;
    private final Uri uri;

    public AboutLink(int textResource, Uri uri, int drawableResource)
    {
        this.textResource = textResource;
        this.uri = uri;
        this.drawableResource = drawableResource;
    }

    public AboutLink(int textResource, String url, int drawableResource)
    {
        this(textResource, Uri.parse(url), drawableResource);
    }

    @Override
    public void setText(TextView textView)
    {
        textView.setText(textResource);
        textView.setCompoundDrawablesWithIntrinsicBounds(drawableResource, 0, 0, 0);
    }

    @Override
    public void onClicked(Activity activity)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
