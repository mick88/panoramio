package com.michaldabski.panoramio.about;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.michaldabski.panoramio.BuildConfig;
import com.michaldabski.panoramio.R;

/**
 * Created by Michal on 14-Sep-14.
 */
public class AboutActivity extends Activity implements AdapterView.OnItemClickListener
{
    private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/developer?id=mick88";
    private static final String GOOGLE_PLUS_URL = "https://plus.google.com/u/0/+MichalDabski";
    private static final String WEBSITE_URL = "http://www.michaldabski.com/";
    private static final String GITHUB_URL = "https://github.com/mick88";
    private static final String SHARE_URL = "https://play.google.com/store/apps/details?id=com.mick88.dittimetable";

    private final AboutItem[] items = new AboutItem[]{
            new AboutItem()
            {
                @Override
                public void setText(TextView textView)
                {
                    textView.setText(R.string.share_app);
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share, 0, 0, 0);
                }

                @Override
                public void onClicked(Activity activity)
                {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_URL);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                            getString(R.string.share_attach_text));
                    activity.startActivity(shareIntent);
                }
            },
            new AboutLink(R.string.playstore_link_title, PLAYSTORE_URL, R.drawable.ic_playstore),
            new AboutLink(R.string.visit_website, WEBSITE_URL, R.drawable.ic_website),
            new AboutLink(R.string.google_plus, GOOGLE_PLUS_URL, R.drawable.ic_google_plus),
            new AboutLink(R.string.github, GITHUB_URL, R.drawable.ic_github),
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupActionbar(getActionBar());
        setupList((ListView) findViewById(android.R.id.list));
    }

    void setupActionbar(ActionBar actionBar)
    {
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    void setupList(ListView listView)
    {
        listView.setOnItemClickListener(this);
        addHeaderView(listView);

        ListAdapter adapter = new AboutItemAdapter(this, items);
        listView.setAdapter(adapter);
    }

    void addHeaderView(ListView listView)
    {
        View headerView = getLayoutInflater().inflate(R.layout.about_header, listView, false);
        listView.addHeaderView(headerView, null, false);

        TextView tvVersion = (TextView) headerView.findViewById(R.id.tvAppVersion);
        tvVersion.setText(getString(R.string.version_s, BuildConfig.VERSION_NAME));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);

        if (item instanceof AboutItem)
            ((AboutItem) item).onClicked(this);
    }
}
