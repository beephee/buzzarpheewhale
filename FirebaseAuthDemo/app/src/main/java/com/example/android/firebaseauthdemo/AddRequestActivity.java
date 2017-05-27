package com.example.android.firebaseauthdemo;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.R.attr.button;
import static android.R.attr.id;
import static com.example.android.firebaseauthdemo.R.id.dateValue;

public class AddRequestActivity extends AppCompatActivity {


    Button buttonNewListing;
    Button buttonGetCoordinates;
    Spinner spinnerProductType;
    DatabaseReference databaseProducts;
    String userEmail;
    String productcoords;
    private static final String TAG = "AddRequestActivity";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date;

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

        mDisplayDate = (TextView) findViewById(dateValue);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddRequestActivity.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

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
        EditText editLength = (EditText) findViewById(R.id.dim1);
        String length = editLength.getText().toString();
        EditText editWidth = (EditText) findViewById(R.id.dim2);
        String width = editWidth.getText().toString();
        EditText editHeight = (EditText) findViewById(R.id.dim3);
        String height = editHeight.getText().toString();
        EditText editWeight = (EditText) findViewById(R.id.weightValue);
        String weight = editWeight.getText().toString();
        EditText editPrice = (EditText) findViewById(R.id.priceValue);
        String price = editPrice.getText().toString();

        if(!TextUtils.isEmpty(producttype) && !TextUtils.isEmpty(productname) && !TextUtils.isEmpty(length) && !TextUtils.isEmpty(width) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight) && !TextUtils.isEmpty(price)){

            //Get the unique id of the branch
            String id = databaseProducts.push().getKey();
            //Define the parameters for the database entry
            Product product = new Product(id, buyer, productname, producttype, productcoords, length, width, height, weight, price, date);
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
