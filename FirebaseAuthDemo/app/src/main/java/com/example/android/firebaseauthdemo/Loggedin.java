package com.example.android.firebaseauthdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.ittianyu.bottomnavigationviewex.*;

import static com.example.android.firebaseauthdemo.R.layout.activity_loggedin;

public class Loggedin extends AppCompatActivity {

    String userEmail;
    String directedPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_loggedin);

        //Grabs email string and page choice from previous activity
        Bundle extras = getIntent().getExtras();
        userEmail = extras.getString("email");
        directedPage = extras.getString("page");

        //Default fragment opened upon activity's creation
        if (savedInstanceState == null) {
            BuyerActivity defaultFrag = new BuyerActivity();
            CourierActivity defaultFrag2 = new CourierActivity();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (directedPage=="buyer") {
                fragmentTransaction.add(R.id.frame_layout, defaultFrag);
            }
            else {
                fragmentTransaction.add(R.id.frame_layout, defaultFrag2);
            }
            fragmentTransaction.commit();
        }

        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //selectedFragment will need to set back to null after other fragments are inserted
                        Fragment selectedFragment = BuyerActivity.newInstance();
                        switch (item.getItemId()) {
                            case R.id.actionBuyer:
                                selectedFragment = BuyerActivity.newInstance();
                                break;
                            case R.id.actionCourier:
                                selectedFragment = CourierActivity.newInstance();
                                break;
                            case R.id.actionChats:
                                selectedFragment = MessagingActivity.newInstance();
                                break;
                            case R.id.actionMaps:
                                selectedFragment = MenuMapActivity.newInstance();
                                break;
                            case R.id.actionSettings:
                                selectedFragment = SettingsActivity.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
    }

}
