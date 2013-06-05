package com.tryrosberry.ololostfilm.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.logic.api.HtmlParser;
import com.tryrosberry.ololostfilm.logic.api.LostFilmRestClient;
import com.tryrosberry.ololostfilm.ui.activities.MainActivity;
import com.tryrosberry.ololostfilm.ui.adapters.NewsAdapter;
import com.tryrosberry.ololostfilm.ui.models.NewsFeedItem;
import com.tryrosberry.ololostfilm.utils.Connectivity;

import java.io.IOException;
import java.util.ArrayList;

public class NewsFragment extends BaseFragment {

    private static final String ARG_POSITION = "position";

    private int position;
    private ListView mSerialListView;
    private ArrayList<NewsFeedItem> mNewsList;
    private NewsAdapter mAdapter;
    private int mPage = 0;
    private boolean loadingContent = false;

    public static NewsFragment newInstance(int position) {
        NewsFragment f = new NewsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list_layout,null);
        mSerialListView = (ListView) v.findViewById(R.id.contentListView);
        if(mSerialListView.getAdapter() == null && mAdapter != null) mSerialListView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(getActivity() != null)getData();
        }

    }

    @Override
    public void getData() {
        if(loadingContent) return;
        if(mNewsList == null || mNewsList.isEmpty()){
            if(getActivity() != null && Connectivity.isConnected(getActivity())){
                LostFilmRestClient.get(mPage == 0 ? "/news.php" : "/news.php?o="+mPage+"0", null, new AsyncHttpResponseHandler() {
                    private ProgressDialog mProgress;

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingContent = true;
                        if (mProgress == null) {
                            mProgress = ProgressDialog.show(getActivity(), null,
                                    "Getting NewsFeed...", true, true);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadingContent = false;
                        if (mProgress != null) {
                            mProgress.dismiss();
                            mProgress = null;
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        if (getActivity() != null) {
                            mNewsList = HtmlParser.parseNews(s);
                            if (mAdapter == null) mAdapter = new NewsAdapter(getActivity(),
                                    mNewsList,((MainActivity)getActivity()).getImageFetcher());
                            else mAdapter.setContent(mNewsList);

                            mSerialListView.setAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                        if (NewsFragment.this.isAdded()) {
                            if (throwable instanceof IOException) {
                                ((MainActivity) getActivity()).showMessage("Error", getString(R.string.error_internet) + "\n" + s);
                            } else ((MainActivity) getActivity()).showMessage("ERROR", throwable.toString());
                        }
                    }
                });
            } else ((MainActivity)getActivity()).showMessage("Error",getString(R.string.error_internet));
        } else {
            if(mAdapter == null){
                mAdapter = new NewsAdapter(getActivity(),
                        mNewsList,((MainActivity)getActivity()).getImageFetcher());
                mSerialListView.setAdapter(mAdapter);
            } else mAdapter.setContent(mNewsList);

        }

    }
}
