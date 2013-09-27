package com.tryrosberry.ololostfilm.ui.fragments.secondary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.logic.api.HtmlParser;
import com.tryrosberry.ololostfilm.logic.api.LostFilmRestClient;
import com.tryrosberry.ololostfilm.ui.fragments.BaseFragment;
import com.tryrosberry.ololostfilm.utils.Connectivity;

import java.io.IOException;

public class NewsDetailFragment extends BaseFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_LINK = "link";
    private WebView mWebView;

    private String title;
    private String link;
    private boolean loadingContent = false;
    private boolean gotDescription = false;

    public static NewsDetailFragment newInstance(String title, String link) {
        NewsDetailFragment f = new NewsDetailFragment();
        Bundle b = new Bundle();
        b.putString(ARG_TITLE, title);
        b.putString(ARG_LINK, link);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getArguments().getString(ARG_TITLE);
        link = getArguments().getString(ARG_LINK);

        getMainActivity().getSupportActionBar().hide();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.web_details,null);
        mWebView = (WebView) v.findViewById(R.id.elementWeb);
        ((TextView)v.findViewById(R.id.elementTitle)).setText(Html.fromHtml(title));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebView.getSettings().setJavaScriptEnabled(true);
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getMainActivity().getSupportActionBar().show();
    }

    @Override
    public void getData() {
        if(loadingContent) return;
        if(!gotDescription){
            if(getActivity() != null && Connectivity.isConnected(getActivity())){
                LostFilmRestClient.get(link, null, new AsyncHttpResponseHandler() {
                    private ProgressDialog mProgress;

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingContent = true;
                        if (mProgress == null) {
                            mProgress = ProgressDialog.show(getActivity(), null,
                                    "Getting Details...", true, true);
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
                            gotDescription = true;
                            mWebView.loadData(HtmlParser.parseNewsDetailsForWebView(s),"text/html; charset=UTF-8",null);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                        if (NewsDetailFragment.this.isAdded()) {
                            if (throwable instanceof IOException) {
                                getMainActivity().showMessage("Error", getString(R.string.error_internet) + "\n" + s);
                            } else getMainActivity().showMessage("ERROR", throwable.toString());
                        }
                    }
                });
            } else getMainActivity().showMessage("Error", getString(R.string.error_internet));
        }

    }

}
