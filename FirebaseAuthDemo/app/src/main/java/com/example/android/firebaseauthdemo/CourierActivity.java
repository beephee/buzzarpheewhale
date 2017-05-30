package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourierActivity extends AppCompatActivity {

    private Button buyerButton;

    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNaviBarCourier);
        bottomNavigationView.setSelectedItemId(R.id.actionCourier);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");

        listViewProducts = (ListView) findViewById(R.id.listViewProducts);

        productList = new ArrayList<>();

        //Grabs email string from previous activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        //Registers product courier as user email
        /*Button buttonAccept = (Button) findViewById(R.id.buttonAcceptReq);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCourier = userEmail;
            }
        });*/

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.actionBuyer:
                                Intent intentCouBuyer = new Intent(CourierActivity.this, BuyerActivity.class);
                                //Pass the email string to next activity
                                intentCouBuyer.putExtra("email", userEmail);
                                startActivity(intentCouBuyer);

                            case R.id.actionCourier:
                                //no change
                                return true;

                            case R.id.actionSettings:
                                Intent intentCouSettings = new Intent(CourierActivity.this, SettingsActivity.class);
                                //Pass the email string to next activity
                                intentCouSettings.putExtra("email", userEmail);
                                startActivity(intentCouSettings);

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

    @Override
    protected void onStart() {
        super.onStart();

        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productList.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                ProductList adapter = new ProductList(CourierActivity.this, productList);
                listViewProducts.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void goBuyerPage(View view)
    {
        Intent intent = new Intent(this, BuyerActivity.class);
        //Pass the email string to next activity
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

}
