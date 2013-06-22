package com.tryrosberry.ololostfilm.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.logic.api.HtmlParser;
import com.tryrosberry.ololostfilm.logic.api.LostFilmRestClient;
import com.tryrosberry.ololostfilm.logic.storage.ConstantStorage;
import com.tryrosberry.ololostfilm.ui.models.Season;
import com.tryrosberry.ololostfilm.ui.models.Serial;
import com.tryrosberry.ololostfilm.ui.models.SerialDetails;
import com.tryrosberry.ololostfilm.ui.models.Series;
import com.tryrosberry.ololostfilm.utils.Connectivity;

import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SerialDetailsFragment extends BaseFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_LINK = "link";
    private LinearLayout mContainer;

    private String title;
    private String link;
    private boolean loadingContent = false;
    private boolean gotDescription = false;
    private ArrayList<Season> mSeasonList;

    public static SerialDetailsFragment newInstance(String title, String link) {
        SerialDetailsFragment f = new SerialDetailsFragment();
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
        mSeasonList = new ArrayList<Season>();
        getMainActivity().getSupportActionBar().hide();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.element_details,null);
        mContainer = (LinearLayout) v.findViewById(R.id.elementContent);
        ((TextView)v.findViewById(R.id.elementTitle)).setText(Html.fromHtml(title));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

                            Serial serial = HtmlParser.getSerialDetails(s);
                            parseDetails(serial);

                            //parseDetails(s);
                            //parse response and create description;
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                        if (SerialDetailsFragment.this.isAdded()) {
                            if (throwable instanceof IOException) {
                                getMainActivity().showMessage("Error", getString(R.string.error_internet) + "\n" + s);
                            } else getMainActivity().showMessage("ERROR", throwable.toString());
                        }
                    }
                });
            } else getMainActivity().showMessage("Error", getString(R.string.error_internet));
        }

    }

    private void parseDetails(Serial serial){

        if(serial != null){
            SerialDetails det = serial.details;
            if(det != null){
                if(!det.imageLink.equals("")){
                    ImageView image = new ImageView(getActivity());
                    image.setPadding(5, 5, 5, 5);
                    getMainActivity().getImageFetcher().loadImage(det.imageLink,image);
                    mContainer.addView(image);
                }

                if(!det.description.equals("")) makeText(det.description,false);

            }
            ArrayList<Season> seasons = serial.seasons;
            if(seasons != null && seasons.size() > 0){
                for(Season season : seasons){
                    makeText(season.name,true);
                    ArrayList<Series> serieses = season.series;
                    if(serieses != null && serieses.size() > 0){
                        for(Series series : serieses){
                            makeText(series.number + " " + series.title,false);
                        }
                    }

                }
            }
        }

    }

    private boolean makeText(TagNode item){
        String textContent = HtmlParser.getContent(item);
        return makeText(textContent,false);
    }

    private boolean makeText(String textContent, boolean big){
        TextView text = new TextView(getActivity());
        text.setAutoLinkMask(Linkify.WEB_URLS);
        if(!textContent.trim().equals("")){
            text.setText(Html.fromHtml(textContent));
            if(big) {
                text.setPadding(0, 15, 0, 0);
                text.setTextAppearance(getActivity(),android.R.style.TextAppearance_Medium);
            }
            mContainer.addView(text);
            return true;
        } else return false;
    }

}
