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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.firebaseauthdemo.R.id.editTextBuyerCountry;
import static com.example.android.firebaseauthdemo.R.id.listViewProducts;

public class BuyerSettingsFragment extends Fragment {

    public static BuyerSettingsFragment newInstance() {
        BuyerSettingsFragment fragment = new BuyerSettingsFragment();
        return fragment;
    }

    String userEmail;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseUsers;
    String userBuyerCountry;
    String userMaxBudget;
    EditText editBuyerCountry;
    EditText editMaxBudget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.buyer_settings, container, false);

        //Grabs email string from previous activity
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            userEmail = extras.getString("email");
        }

        Button updateSettings = (Button) rootView.findViewById(R.id.updateBuyer);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userBuyerCountry = dataSnapshot.child("buyerCountry").getValue(String.class);
                userMaxBudget = dataSnapshot.child("maxBudget").getValue(String.class);
                editBuyerCountry = (EditText) rootView.findViewById(R.id.editTextBuyerCountry);
                editMaxBudget = (EditText) rootView.findViewById(R.id.editTextMaxBudget);
                editBuyerCountry.setText(userBuyerCountry);
                editMaxBudget.setText(userMaxBudget);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newBuyerCountry = editBuyerCountry.getText().toString();
                String newMaxBudget = editMaxBudget.getText().toString();
                if (newBuyerCountry.equals("") || newMaxBudget.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill up all fields to update settings!", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("buyerCountry");
                    dR.setValue(newBuyerCountry);
                    DatabaseReference dR2 = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("maxBudget");
                    dR2.setValue(newMaxBudget);
                    Toast.makeText(getActivity().getApplicationContext(), "Buyer settings updated!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

}