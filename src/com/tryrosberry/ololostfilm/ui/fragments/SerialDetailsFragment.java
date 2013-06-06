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
import com.tryrosberry.ololostfilm.utils.Connectivity;

import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.util.List;

public class SerialDetailsFragment extends BaseFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_LINK = "link";
    private LinearLayout mContainer;

    private String title;
    private String link;
    private boolean loadingContent = false;
    private boolean gotDescription = false;

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
                            parseDetails(s);
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

    private void parseDetails(String s){
        List<TagNode> nodes = HtmlParser.parseSerialDetails(s);
        if(nodes.size() >= 2){
            TagNode serialDescriptionNode = nodes.get(0);
            if(serialDescriptionNode != null){
                ImageView image = new ImageView(getActivity());
                image.setPadding(5, 5, 5, 5);
                String url = ConstantStorage.BASE_URL + HtmlParser.getLinksByClass(serialDescriptionNode,"img").get(0).getAttributeByName("src");
                if(URLUtil.isNetworkUrl(url)){
                    getMainActivity().getImageFetcher().loadImage(url,image);
                    mContainer.addView(image);
                }
                makeText(serialDescriptionNode);
            }

            TagNode serialTorrListNode = nodes.get(1);
            if(serialTorrListNode != null){
                List<TagNode> torrentsNodes = HtmlParser.getLinksByClass(serialTorrListNode,"div");
                if(torrentsNodes.size() > 0){
                    for(TagNode torNod : torrentsNodes){
                        String classType = torNod.getAttributeByName("class");
                        if (classType != null){
                            //create a ll with 1 season (inflate)
                            boolean hasContent = false;
                            if(classType.equals("content")){
                               hasContent = makeText(torNod);
                            } else if(classType.contains("t_row")){
                               List<TagNode> numbers = HtmlParser.getLinksByClass(torNod,"td","class","t_episode_num");
                               hasContent = makeText(numbers.get(0));
                               List<TagNode> titles = HtmlParser.getLinksByClass(torNod,"nobr",true);
                               hasContent = makeText(titles.get(0));
                            }

                            /*if (hasContent){
                                mContainer.addView(season);
                            }*/
                        }
                    }
                }
            }

        }
    }

    private boolean makeText(TagNode item){
        TextView text = new TextView(getActivity());
        text.setAutoLinkMask(Linkify.WEB_URLS);
        String textContent = HtmlParser.getContent(item);
        if(!textContent.trim().equals("")){
            text.setText(Html.fromHtml(textContent));
            mContainer.addView(text);
            return true;
        } else return false;
    }

}
