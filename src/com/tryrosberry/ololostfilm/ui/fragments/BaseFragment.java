package com.tryrosberry.ololostfilm.ui.fragments;

import android.support.v4.app.Fragment;

import com.tryrosberry.ololostfilm.ui.activities.MainActivity;

public class BaseFragment extends Fragment {

    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

    public void getData(){}

}
