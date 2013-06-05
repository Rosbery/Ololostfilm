package com.tryrosberry.ololostfilm.ui.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.imagefatcher.ImageFetcher;
import com.tryrosberry.ololostfilm.ui.models.NewsFeedItem;

import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private Context mContext;
    private List<NewsFeedItem> mContent;
    private ImageFetcher mFatcher;

    public NewsAdapter(Context context, List<NewsFeedItem> content,ImageFetcher fetcher){
        mContext = context;
        mContent = content;
        mFatcher = fetcher;
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

        NewsViewHolder viewHolder;

        final NewsFeedItem pickedNewsFeed = (NewsFeedItem) getItem(position);

        if (rootView == null) {
            viewHolder = new NewsViewHolder();
            rootView = LayoutInflater.from(mContext).inflate(R.layout.news_row, null, false);
            rootView.setTag(viewHolder);

            viewHolder.image = (ImageView) rootView.findViewById(R.id.newsImage);
            viewHolder.tile = (TextView) rootView.findViewById(R.id.newsTitle);
            viewHolder.description = (TextView) rootView.findViewById(R.id.newsDescription);

            LinearLayout.LayoutParams imgParams = (LinearLayout.LayoutParams) viewHolder.image.getLayoutParams();
            imgParams.height = mContext.getResources().getDisplayMetrics().widthPixels/2;
            viewHolder.image.setLayoutParams(imgParams);

        } else viewHolder = (NewsViewHolder) rootView.getTag();

        if(mFatcher != null)mFatcher.loadImage(pickedNewsFeed.image,viewHolder.image);
        viewHolder.tile.setText(Html.fromHtml(pickedNewsFeed.title));
        viewHolder.description.setText(Html.fromHtml(pickedNewsFeed.description));

        return rootView;
    }

    static class NewsViewHolder {
        ImageView image;
        TextView tile;
        TextView description;
    }

    public void setContent(List<NewsFeedItem> content){
        mContent = content;
    }

    public List<NewsFeedItem> getContent(){
        return mContent;
    }

}

