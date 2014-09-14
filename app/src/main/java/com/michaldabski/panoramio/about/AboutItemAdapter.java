package com.michaldabski.panoramio.about;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.michaldabski.panoramio.R;

/**
 * Created by Michal on 14-Sep-14.
 */
public class AboutItemAdapter extends BaseAdapter
{
    private final Context context;
    private final AboutItem[] items;

    public AboutItemAdapter(Context context, AboutItem[] items)
    {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount()
    {
        return items.length;
    }

    @Override
    public Object getItem(int position)
    {
        return items[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.about_item, parent, false);
        items[position].setText((TextView) view);
        return view;
    }
}
