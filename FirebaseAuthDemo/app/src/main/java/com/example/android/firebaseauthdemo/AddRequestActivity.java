package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.R.attr.button;

public class AddRequestActivity extends AppCompatActivity {


    Button buttonNewListing;
    Button buttonGetCoordinates;
    Spinner spinnerProductType;
    DatabaseReference databaseProducts;
    String userEmail;
    String productcoords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);
        //Grabs email string from previous activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        buttonGetCoordinates = (Button) findViewById(R.id.buttonGetCoordinates);
        buttonGetCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPointOnMap();
            }
        });

        //Values will be submitted to the "products" node in the JSON tree
        databaseProducts = FirebaseDatabase.getInstance().getReference("products");

        buttonNewListing = (Button) findViewById(R.id.buttonNewListing);
        buttonNewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                addProductType();
            }
        });

        spinnerProductType = (Spinner) findViewById(R.id.spinnerProductType);

    }

    static final int PICK_MAP_POINT_REQUEST = 999;  // The request code
    private void pickPointOnMap() {
        Intent pickPointIntent = new Intent(this, MapsActivity.class);
        startActivityForResult(pickPointIntent, PICK_MAP_POINT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_MAP_POINT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                LatLng latLng = (LatLng) data.getParcelableExtra("picked_point");
                Toast.makeText(this, "Point Chosen: " + latLng.latitude + " " + latLng.longitude, Toast.LENGTH_LONG).show();
                buttonGetCoordinates.setText(latLng.latitude + "," + latLng.longitude);
                productcoords = (latLng.latitude + "," + latLng.longitude);
            }
        }
    }

    private void addProductType(){

        String buyer = userEmail;
        String producttype = spinnerProductType.getSelectedItem().toString();
        EditText editProductName = (EditText) findViewById(R.id.editTextProductName);
        String productname = editProductName.getText().toString();

        if(!TextUtils.isEmpty(producttype) && !TextUtils.isEmpty(productname)){

            //Get the unique id of the branch
            String id = databaseProducts.push().getKey();
            //Define the parameters for the database entry
            Product product = new Product(id, buyer, productname, producttype, productcoords);
            //Submit value to database
            databaseProducts.child(id).setValue(product);
            Toast.makeText(this, "Request added!", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this, "Please ensure all fields are completed.", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, BuyerActivity.class);
        startActivity(intent);
    }

}
