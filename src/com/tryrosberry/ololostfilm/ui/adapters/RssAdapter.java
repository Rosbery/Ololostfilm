package com.tryrosberry.ololostfilm.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.imagefatcher.ImageFetcher;
import com.tryrosberry.ololostfilm.ui.models.RssItem;

import java.util.List;

public class RssAdapter extends BaseAdapter {

    private Context mContext;
    private List<RssItem> mContent;
    private ImageFetcher mFatcher;

    public RssAdapter(Context context, List<RssItem> content/*,ImageFetcher fetcher*/){
        mContext = context;
        mContent = content;
        //mFatcher = fetcher;
    }

    @Override
    public int getCount() {
        return mContent.size();
    }

    @Override
    public Object getItem(int position) {
        return mContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View rootView, ViewGroup parent) {

        RssViewHolder viewHolder;

        final RssItem pickedRssItem = (RssItem) getItem(position);

        if (rootView == null) {
            viewHolder = new RssViewHolder();
            rootView = LayoutInflater.from(mContext).inflate(R.layout.rss_row, null);
            rootView.setTag(viewHolder);

            viewHolder.title = (TextView) rootView.findViewById(R.id.rssTitle);
            viewHolder.date = (TextView) rootView.findViewById(R.id.rssDate);

        } else viewHolder = (RssViewHolder) rootView.getTag();

        viewHolder.title.setText(pickedRssItem.title);
        viewHolder.date.setText(pickedRssItem.pubDate);

        return rootView;
    }

    static class RssViewHolder {
        TextView title;
        TextView date;

    }

    public void setContent(List<RssItem> content){
        mContent = content;
        notifyDataSetChanged();
    }

    public List<RssItem> getContent(){
        return mContent;
    }

}
