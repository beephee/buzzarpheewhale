package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class BuyerActivity extends AppCompatActivity {

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);
        //Grabs email string from previous activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }
    }

    //Goto Courier Dashboard
    public void viewAddRequest(View view)
    {
        Intent intent = new Intent(this, AddRequestActivity.class);
        //Pass the email string to next activity
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    public void goCourierPage(View view)
    {
        Intent intent = new Intent(this, CourierActivity.class);
        startActivity(intent);
    }

}
