package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.ittianyu.bottomnavigationviewex.*;


public class SettingsActivity extends AppCompatActivity {

    String userEmail;
    BottomNavigationViewEx bottomNavigationViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        BottomNavigationViewEx bottomNavigationViewSettings = (BottomNavigationViewEx) findViewById(R.id.bottomNaviBarSettings);
        bottomNavigationViewSettings.setSelectedItemId(R.id.actionSettings);
        bottomNavigationViewSettings.enableAnimation(false);
        bottomNavigationViewSettings.enableShiftingMode(false);
        bottomNavigationViewSettings.enableItemShiftingMode(false);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        bottomNavigationViewSettings.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.actionBuyer:
                                Intent intentSetBuyer = new Intent(SettingsActivity.this, BuyerActivity.class);
                                //Pass the email string to next activity
                                intentSetBuyer.putExtra("email", userEmail);
                                startActivity(intentSetBuyer);

                            case R.id.actionCourier:
                                Intent intentSetCourier = new Intent(SettingsActivity.this, CourierActivity.class);
                                //Pass the email string to next activity
                                intentSetCourier.putExtra("email", userEmail);
                                startActivity(intentSetCourier);

                            case R.id.actionChats:
                                //to change
                                return true;

                            case R.id.actionMaps:
                                //to change
                                return true;

                        }
                        return true;
                    }
                });

    }
}
