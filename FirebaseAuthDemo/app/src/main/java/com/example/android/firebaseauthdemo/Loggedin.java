package com.example.android.firebaseauthdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.ittianyu.bottomnavigationviewex.*;

import static com.example.android.firebaseauthdemo.R.id.textViewMyOrders;
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

        //Initialize navbar first as a method of it needs to be called in fragment selection
        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);


        //Default fragment opened upon activity's creation
        if (savedInstanceState == null) {
            BuyerActivity defaultFrag = new BuyerActivity();
            CourierActivity defaultFrag2 = new CourierActivity();
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

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment selectedFragment = null;
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
