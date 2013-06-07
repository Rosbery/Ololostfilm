package com.tryrosberry.ololostfilm.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.logic.api.HtmlParser;
import com.tryrosberry.ololostfilm.logic.api.LostFilmRestClient;
import com.tryrosberry.ololostfilm.utils.Connectivity;

import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.util.List;

public class NewsDetailFragment extends BaseFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_LINK = "link";
    private LinearLayout mContainer;

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

    private void parseDetails(String s){
        List<TagNode> nodes = HtmlParser.parseNewsDetails(s);
        if(nodes.size() >= 1){
            TagNode newsRootNode = nodes.get(0);
            TagNode newsContent = newsRootNode;
            List <TagNode> descriptItems = newsContent.getChildTagList();
            //new versting
            List<TagNode> newVerstNews = HtmlParser.getLinksByClass(newsContent, "div", "class", "news-container");
            if(newVerstNews.size() >= 1) {
                newsContent = newVerstNews.get(0);
                List <TagNode> newDescriptItems = newsContent.getChildTagList();
                newDescriptItems.addAll(descriptItems);
                descriptItems = newDescriptItems;
            }
            //
            if(descriptItems.size() > 0){
                int textCounter = 0;
                for(int i = 0; i < descriptItems.size();i++){
                    TagNode item = descriptItems.get(i);
                    if(item.getName().equals("p")){
                        if(textCounter != 0 && !HtmlParser.getContent(item).contains("Дата")){
                            if(makeText(item)) textCounter++;
                        }
                    } else if(item.getName().equals("div")){
                        String classType = item.getAttributeByName("class");
                        if(classType != null && classType.equals("center")){
                            if(makeText(item)) textCounter++;
                            ImageView image = new ImageView(getActivity());
                            image.setPadding(5,5,5,5);
                            List<TagNode> imageUrls = HtmlParser.getLinksByClass(item, "img");
                            if(imageUrls.size() > 0){
                                getMainActivity().getImageFetcher().loadImage(
                                        imageUrls.get(0).getAttributeByName("src"),image);
                                mContainer.addView(image);
                            }

                        }

                    }

                }

                if(textCounter <= 1){
                    makeText(newsRootNode);
                }

            }

        }
    }

    private boolean makeText(TagNode item){
        TextView text = new TextView(getActivity());
        String textContent = HtmlParser.getContent(item);
        if(!textContent.trim().equals("")){
            text.setText(Html.fromHtml(textContent));
            mContainer.addView(text);
            return true;
        } else return false;
    }

}
