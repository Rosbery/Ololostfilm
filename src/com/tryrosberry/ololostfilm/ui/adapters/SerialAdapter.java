package com.tryrosberry.ololostfilm.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.imagefatcher.ImageFetcher;
import com.tryrosberry.ololostfilm.ui.activities.MainActivity;
import com.tryrosberry.ololostfilm.ui.fragments.SerialDetailsFragment;
import com.tryrosberry.ololostfilm.ui.models.Serial;

import java.util.List;


public class SerialAdapter extends BaseAdapter {

    private Context mContext;
    private List<Serial> mContent;
    private ImageFetcher mFatcher;

    public SerialAdapter(Context context, List<Serial> content/*,ImageFetcher fetcher*/){
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

        SerialViewHolder viewHolder;

        final Serial pickedSerial = (Serial) getItem(position);

        if (rootView == null) {
            viewHolder = new SerialViewHolder();
            rootView = LayoutInflater.from(mContext).inflate(R.layout.serial_row, null, false);
            rootView.setTag(viewHolder);

            viewHolder.name = (TextView) rootView.findViewById(R.id.serialName);
            viewHolder.subName = (TextView) rootView.findViewById(R.id.serialSubName);

        } else viewHolder = (SerialViewHolder) rootView.getTag();

        viewHolder.name.setText((position+1) + ". " + Html.fromHtml(pickedSerial.name));
        viewHolder.subName.setText(pickedSerial.subName);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SerialDetailsFragment.newInstance(pickedSerial.name, pickedSerial.url);

                FragmentManager fragmentManager = ((MainActivity)mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(android.R.id.content, fragment)
                        .addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    static class SerialViewHolder {
        TextView name;
        TextView subName;
    }

    public void setContent(List<Serial> content){
        mContent = content;
    }

    public List<Serial> getContent(){
        return mContent;
    }

}
