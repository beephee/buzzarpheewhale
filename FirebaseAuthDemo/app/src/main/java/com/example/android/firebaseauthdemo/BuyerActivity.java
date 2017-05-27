package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuyerActivity extends AppCompatActivity {

    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");

        listViewProducts = (ListView) findViewById(R.id.listViewProducts);

        productList = new ArrayList<>();

        //Grabs email string from previous activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }
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
                    //Filter results to show only products by the user
                    String email = product.getProductBuyer();
                    if(email.equals(userEmail)){
                        productList.add(product);
                    }
                }

                ProductListBuyer adapter = new ProductListBuyer(BuyerActivity.this, productList);
                listViewProducts.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        //Pass the email string to next activity
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

}
