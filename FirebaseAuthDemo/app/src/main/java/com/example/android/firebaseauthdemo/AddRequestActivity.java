package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddRequestActivity extends AppCompatActivity {


    Button buttonNewListing;
    Spinner spinnerProductType;
    DatabaseReference databaseProducts;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);
        //Grabs email string from previous activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        //Values will be submitted to the "products" node in the JSON tree
        databaseProducts = FirebaseDatabase.getInstance().getReference("products");

        buttonNewListing = (Button) findViewById(R.id.buttonNewListing);
        spinnerProductType = (Spinner) findViewById(R.id.spinnerProductType);

        buttonNewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                addProductType();
            }
        });

    }

    public void selectCoords(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void addProductType(){
        String buyer = userEmail;
        String producttype = spinnerProductType.getSelectedItem().toString();

        if(!TextUtils.isEmpty(producttype)){

            //Get the unique id of the branch
            String id = databaseProducts.push().getKey();
            //Define the parameters for the database entry
            Product product = new Product(id, buyer, producttype);
            //Submit value to database
            databaseProducts.child(id).setValue(product);
            Toast.makeText(this, "Request added!", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this, "Please select a product type.", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, BuyerActivity.class);
        startActivity(intent);
    }

}
