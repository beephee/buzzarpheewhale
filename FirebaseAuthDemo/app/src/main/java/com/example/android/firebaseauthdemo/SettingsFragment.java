package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.android.firebaseauthdemo.R.id.button;
import static com.example.android.firebaseauthdemo.R.id.buttonAdmin;
import static com.example.android.firebaseauthdemo.R.id.listViewProducts;


public class SettingsFragment extends Fragment {

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    String userEmail;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUsers;
    View buttonView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        Button btnAdmin = (Button) rootView.findViewById(R.id.buttonAdmin);
        btnAdmin.setOnClickListener(AdminButtonClickListener);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //User users = dataSnapshot.getValue(User.class);
                String userType = dataSnapshot.child("userType").getValue(String.class);
                if(userType.equals("admin")){
                    buttonView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        buttonView = (View) getView().findViewById(buttonAdmin);
    }

    private View.OnClickListener AdminButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AdminActivity.class);
            getActivity().startActivity(intent);
        }
    };
}
