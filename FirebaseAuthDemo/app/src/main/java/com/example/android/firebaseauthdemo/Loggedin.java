package com.example.android.firebaseauthdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.ittianyu.bottomnavigationviewex.*;

public class Loggedin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        //Default fragment opened upon activity's creation
        if (savedInstanceState == null) {
            BuyerActivity defaultFrag = new BuyerActivity();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_layout, defaultFrag);
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
                                //selectedFragment = CourierActivity.newInstance();
                                break;
                            case R.id.actionChats:
                                //selectedFragment = ChatActivity.newInstance();
                                break;
                            case R.id.actionMaps:
                                //selectedFragment = BigMapsActivity.newInstance();
                                break;
                            case R.id.actionSettings:
                                //selectedFragment = SettingsActivity.newInstance();
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
