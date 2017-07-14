package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseUsers;
    private String UID;
    private String uemail;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;
    String userEmail;
    String doneTutorial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //adding listener to button
        buttonLogout.setOnClickListener(this);

        //Check if banned
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        UID = user.getUid();
        databaseUsers.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //User users = dataSnapshot.getValue(User.class);
                String isBanned = dataSnapshot.child("blacklisted").getValue(String.class);
                doneTutorial = dataSnapshot.child("tutorial").getValue(String.class);
                if(isBanned.equals("true")){
                    showBannedDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //displaying logged in user name
        textViewUserEmail.setText("Welcome "+ user.getEmail().split("@", 2)[0] + "!\nWhat would you like to do today?");
        userEmail = user.getEmail();
    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    //Goto Courier Dashboard
    public void viewCourierPage(View view)
    {
        Intent intent = new Intent(this, LoggedinActivity.class);
        //Pass the email string and page choice to next activity
        Bundle extras = new Bundle();
        extras.putString("email", userEmail);
        if(doneTutorial.equals("1")) {
            extras.putString("page", "courier");
        } else {
            extras.putString("page", "buyer");
        }
        intent.putExtras(extras);
        startActivity(intent);
    }

    //Goto Buyer Dashboard
    public void viewBuyerPage(View view)
    {
        Intent intent = new Intent(this, LoggedinActivity.class);
        //Pass the email string and page choice to next activity
        Bundle extras = new Bundle();
        extras.putString("email", userEmail);
        extras.putString("page", "buyer");
        intent.putExtras(extras);
        startActivity(intent);
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
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
