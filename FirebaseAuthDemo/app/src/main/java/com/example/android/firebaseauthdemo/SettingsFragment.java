package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
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

        TextView username = (TextView) rootView.findViewById(R.id.accountText);
        username.setText(userEmail);

        Button btnAdmin = (Button) rootView.findViewById(R.id.buttonAdmin);
        btnAdmin.setOnClickListener(AdminButtonClickListener);

        Button btnCourier = (Button) rootView.findViewById(R.id.courierSettings);
        btnCourier.setOnClickListener(CourierButtonClickListener);

        Button btnBuyer = (Button) rootView.findViewById(R.id.buyerSettings);
        btnBuyer.setOnClickListener(BuyerButtonClickListener);

        Button btnLogOut = (Button) rootView.findViewById(R.id.logOut);
        btnLogOut.setOnClickListener(logOutButtonClickListener);

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

    private View.OnClickListener CourierButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Fragment newFrag = new CourierSettingsFragment();
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            trans.replace(R.id.frame_layout, newFrag);
            trans.addToBackStack(null);
            trans.commit();
        }
    };

    private View.OnClickListener BuyerButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Fragment newFrag = new BuyerSettingsFragment();
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            trans.replace(R.id.frame_layout, newFrag);
            trans.addToBackStack(null);
            trans.commit();
        }
    };

    private View.OnClickListener logOutButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    };
}
