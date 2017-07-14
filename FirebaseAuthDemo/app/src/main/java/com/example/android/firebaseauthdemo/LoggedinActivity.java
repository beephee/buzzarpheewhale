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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

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
    BottomNavigationViewEx bottomNavigationView;

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

                String tutCompleted = dataSnapshot.child("tutorial").getValue(String.class);
                if(tutCompleted.equals("0")){
                    showTutorial();
                    tutorialCompleted();
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
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);

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

    //If user logs in for the first time
    public void showTutorial() {

        //Buyer Fragment
        final RelativeLayout tutScreen0 = (RelativeLayout) findViewById(R.id.tutScreen0);
        Button btnMessage0 = (Button) findViewById(R.id.btnMessage0);
        tutScreen0.setVisibility(View.VISIBLE);

        final RelativeLayout tutScreen1 = (RelativeLayout) findViewById(R.id.tutScreen1);
        Button btnMessage1 = (Button) findViewById(R.id.btnMessage1);

        final RelativeLayout tutScreen2 = (RelativeLayout) findViewById(R.id.tutScreen2);
        Button btnMessage2 = (Button) findViewById(R.id.btnMessage2);

        final RelativeLayout tutScreen11 = (RelativeLayout) findViewById(R.id.tutScreen11);
        Button btnMessage11 = (Button) findViewById(R.id.btnMessage11);

        //Courier Fragment
        final RelativeLayout tutScreen3 = (RelativeLayout) findViewById(R.id.tutScreen3);
        Button btnMessage3 = (Button) findViewById(R.id.btnMessage3);

        final RelativeLayout tutScreen4 = (RelativeLayout) findViewById(R.id.tutScreen4);
        Button btnMessage4 = (Button) findViewById(R.id.btnMessage4);

        //Chat Fragment
        final RelativeLayout tutScreen5 = (RelativeLayout) findViewById(R.id.tutScreen5);
        Button btnMessage5 = (Button) findViewById(R.id.btnMessage5);

        final RelativeLayout tutScreen6 = (RelativeLayout) findViewById(R.id.tutScreen6);
        Button btnMessage6 = (Button) findViewById(R.id.btnMessage6);

        //Map Fragment
        final RelativeLayout tutScreen7 = (RelativeLayout) findViewById(R.id.tutScreen7);
        Button btnMessage7 = (Button) findViewById(R.id.btnMessage7);

        final RelativeLayout tutScreen8 = (RelativeLayout) findViewById(R.id.tutScreen8);
        Button btnMessage8 = (Button) findViewById(R.id.btnMessage8);

        //Settings Fragment
        final RelativeLayout tutScreen9 = (RelativeLayout) findViewById(R.id.tutScreen9);
        Button btnMessage9 = (Button) findViewById(R.id.btnMessage9);

        final RelativeLayout tutScreen10 = (RelativeLayout) findViewById(R.id.tutScreen10);
        Button btnMessage10 = (Button) findViewById(R.id.btnMessage10);

        //Button Sequence
        btnMessage0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen0.setVisibility(View.INVISIBLE);
                tutScreen1.setVisibility(View.VISIBLE);
            }
        });
        btnMessage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen1.setVisibility(View.INVISIBLE);
                tutScreen2.setVisibility(View.VISIBLE);
            }
        });
        btnMessage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen2.setVisibility(View.INVISIBLE);
                tutScreen3.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                transaction.replace(R.id.frame_layout, AcceptedCourierFragment.newInstance());
                transaction.commit();
                bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
                bottomNavigationView.setSelectedItemId(R.id.actionCourier);
            }
        });
        btnMessage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen3.setVisibility(View.INVISIBLE);
                tutScreen4.setVisibility(View.VISIBLE);
            }
        });
        btnMessage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen4.setVisibility(View.INVISIBLE);
                tutScreen5.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                transaction.replace(R.id.frame_layout, MessagingFragment.newInstance());
                transaction.commit();
                bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
                bottomNavigationView.setSelectedItemId(R.id.actionChats);
            }
        });
        btnMessage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen5.setVisibility(View.INVISIBLE);
                tutScreen6.setVisibility(View.VISIBLE);
            }
        });
        btnMessage6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen6.setVisibility(View.INVISIBLE);
                tutScreen7.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                transaction.replace(R.id.frame_layout, MenuMapFragment.newInstance());
                transaction.commit();
                bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
                bottomNavigationView.setSelectedItemId(R.id.actionMaps);
            }
        });
        btnMessage7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen7.setVisibility(View.INVISIBLE);
                tutScreen8.setVisibility(View.VISIBLE);
            }
        });
        btnMessage8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen8.setVisibility(View.INVISIBLE);
                tutScreen9.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                transaction.replace(R.id.frame_layout, SettingsFragment.newInstance());
                transaction.commit();
                bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
                bottomNavigationView.setSelectedItemId(R.id.actionSettings);
            }
        });
        btnMessage9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen9.setVisibility(View.INVISIBLE);
                tutScreen10.setVisibility(View.VISIBLE);
            }
        });
        btnMessage10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen10.setVisibility(View.INVISIBLE);
                tutScreen11.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                transaction.replace(R.id.frame_layout, BuyerFragment.newInstance());
                transaction.commit();
                bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
                bottomNavigationView.setSelectedItemId(R.id.actionBuyer);
            }
        });
        btnMessage11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutScreen11.setVisibility(View.INVISIBLE);
            }
        });
    }

    //Set tutorial as done
    public void tutorialCompleted(){
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).child("tutorial").setValue("1");
    }
}
