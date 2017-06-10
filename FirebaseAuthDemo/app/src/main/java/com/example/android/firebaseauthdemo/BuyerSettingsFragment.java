package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.firebaseauthdemo.R.id.listViewProducts;

public class BuyerSettingsFragment extends Fragment {

    public static BuyerSettingsFragment newInstance() {
        BuyerSettingsFragment fragment = new BuyerSettingsFragment();
        return fragment;
    }

    String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.buyer_settings, container, false);

        //Grabs email string from previous activity
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            userEmail = extras.getString("email");
        }
        return rootView;
    }

}