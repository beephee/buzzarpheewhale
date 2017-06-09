package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.*;

import static com.example.android.firebaseauthdemo.R.layout.activity_loggedin;

public class LoggedinActivity extends AppCompatActivity {

    String userEmail;
    String directedPage;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_loggedin);

        //Check if banned
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //User users = dataSnapshot.getValue(User.class);
                String isBanned = dataSnapshot.child("blacklisted").getValue(String.class);
                if(isBanned.equals("true")){
                    showBannedDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Grabs email string and page choice from previous activity
        Bundle extras = getIntent().getExtras();
        userEmail = extras.getString("email");
        directedPage = extras.getString("page");

        //Initialize navbar first as a method of it needs to be called in fragment selection
        final BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);

        //Default fragment opened upon activity's creation
        if (savedInstanceState == null) {
            BuyerFragment defaultFrag = new BuyerFragment();
            AcceptedCourierFragment defaultFrag2 = new AcceptedCourierFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (directedPage.equals("buyer")) {
                fragmentTransaction.add(R.id.frame_layout, defaultFrag);
                bottomNavigationView.setSelectedItemId(R.id.actionBuyer);
            }
            else {
                fragmentTransaction.add(R.id.frame_layout, defaultFrag2);
                bottomNavigationView.setSelectedItemId(R.id.actionCourier);
            }
            fragmentTransaction.commit();
        }

        //Toast.makeText(this, String.valueOf(bottomNavigationView.getSelectedItemId()), Toast.LENGTH_LONG).show();

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.actionBuyer:
                                selectedFragment = BuyerFragment.newInstance();
                                break;
                            case R.id.actionCourier:
                                selectedFragment = AcceptedCourierFragment.newInstance();
                                break;
                            case R.id.actionChats:
                                selectedFragment = MessagingFragment.newInstance();
                                break;
                            case R.id.actionMaps:
                                selectedFragment = MenuMapFragment.newInstance();
                                break;
                            case R.id.actionSettings:
                                selectedFragment = SettingsFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        if(bottomNavigationView.getSelectedItemId() > item.getItemId()){
                            transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                        } else if(bottomNavigationView.getSelectedItemId() == item.getItemId()){
                            return true;
                        }
                        else {
                            transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        }
                        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
    }

    //If user is banned
    public void showBannedDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.banned_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonOk = (Button) dialogView.findViewById(R.id.buttonOk);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
                firebaseAuth.signOut();
                finish();
                Intent intent = new Intent(LoggedinActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
