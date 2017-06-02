package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;
    String userEmail;

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

        //displaying logged in user name
        textViewUserEmail.setText("Welcome "+user.getEmail().split("@", 2)[0]);
        userEmail = user.getEmail();

        //adding listener to button
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    //Goto Courier Dashboard
    public void viewCourierPage(View view)
    {
        Intent intent = new Intent(this, Loggedin.class);
        //Pass the email string and page choice to next activity
        Bundle extras = new Bundle();
        extras.putString("email", userEmail);
        extras.putString("page", "courier");
        intent.putExtras(extras);
        startActivity(intent);
    }

    //Goto Buyer Dashboard
    public void viewBuyerPage(View view)
    {
        Intent intent = new Intent(this, Loggedin.class);
        //Pass the email string and page choice to next activity
        Bundle extras = new Bundle();
        extras.putString("email", userEmail);
        extras.putString("page", "buyer");
        intent.putExtras(extras);
        startActivity(intent);
    }
}
