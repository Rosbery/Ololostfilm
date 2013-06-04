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
import com.tryrosberry.ololostfilm.ui.adapters.SerialAdapter;
import com.tryrosberry.ololostfilm.ui.models.Serial;
import com.tryrosberry.ololostfilm.utils.Connectivity;

import java.io.IOException;
import java.util.ArrayList;

public class SerialFragment extends BaseFragment {

    private static final String ARG_POSITION = "position";

    private int position;
    private ListView mSerialListView;
    private ArrayList<Serial> mSerialList;
    private SerialAdapter mAdapter;

    public static SerialFragment newInstance(int position) {
        SerialFragment f = new SerialFragment();
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
        if(mSerialList == null || mSerialList.isEmpty()){
            if(getActivity() != null && Connectivity.isConnected(getActivity())){
                LostFilmRestClient.get("/serials.php", null, new AsyncHttpResponseHandler() {
                    private ProgressDialog mProgress;

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (mProgress == null) {
                            mProgress = ProgressDialog.show(getActivity(), null,
                                    "Getting Serial List...", true, false);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mProgress != null) {
                            mProgress.dismiss();
                            mProgress = null;
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        if (getActivity() != null) {
                            mSerialList = HtmlParser.parseSerials(s);
                            if(mAdapter == null) mAdapter = new SerialAdapter(getActivity(),
                                    mSerialList/*,mImageFetcher*/);
                            else mAdapter.setContent(mSerialList);

                            mSerialListView.setAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                        if (SerialFragment.this.isAdded()) {
                            if (throwable instanceof IOException) {
                                ((MainActivity) getActivity()).showMessage("Error", getString(R.string.error_internet) + "\n" + s);
                            } else ((MainActivity) getActivity()).showMessage("ERROR", throwable.toString());
                        }
                    }
                });
            } else ((MainActivity)getActivity()).showMessage("Error",getString(R.string.error_internet));
        } else {
            if(mAdapter == null){
                mAdapter = new SerialAdapter(getActivity(),
                        mSerialList/*,mImageFetcher*/);
                mSerialListView.setAdapter(mAdapter);
            } else mAdapter.setContent(mSerialList);

        }

    }
}
