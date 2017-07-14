package com.example.android.firebaseauthdemo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CourierSettingsFragment extends Fragment {

    public static BuyerSettingsFragment newInstance() {
        BuyerSettingsFragment fragment = new BuyerSettingsFragment();
        return fragment;
    }

    String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.courier_settings, container, false);

        //Grabs email string from previous activity
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            userEmail = extras.getString("email");
        }
        return rootView;
    }
}