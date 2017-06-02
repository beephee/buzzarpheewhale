package com.example.android.firebaseauthdemo;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.R.attr.button;
import static android.R.attr.id;
import static com.example.android.firebaseauthdemo.R.id.dateValue;

public class AddRequestActivity extends AppCompatActivity {


    Button buttonNewListing;
    Button buttonGetCoordinates;
    Spinner spinnerProductType;
    ScrollView productScrollView;

    //Image Storage Variables
    Button buttonGetImage;
    DatabaseReference databaseProducts;
    StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    Uri downloadUrl;
    ImageView productImage;
    TextView textViewImageName;

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
                addProduct();
            }
        });

        spinnerProductType = (Spinner) findViewById(R.id.spinnerProductType);

        productScrollView = (ScrollView) findViewById(R.id.ScrollView01);

        mDisplayDate = (TextView) findViewById(dateValue);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View view) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                int year = cal.get(java.util.Calendar.YEAR);
                int month = cal.get(java.util.Calendar.MONTH);
                int day = cal.get(java.util.Calendar.DAY_OF_MONTH);

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

        //Image Upload
        textViewImageName = (TextView) findViewById(R.id.textViewImageName);
        productImage = (ImageView) findViewById(R.id.imageViewProduct);
        mProgressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        buttonGetImage = (Button) findViewById(R.id.buttonGetImage);
        buttonGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

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
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();
            Uri uri = data.getData();
            final String fileName = uri.getPath().toString();
            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    mProgressDialog.dismiss();
                    Toast.makeText(AddRequestActivity.this, "Image uploaded!", Toast.LENGTH_LONG).show();
                    // buttonGetImage.setText(downloadUrl.toString()); //Temporary, just for checking if its uploaded
                    textViewImageName.setText(fileName);
                    buttonGetImage.setText("SELECT ANOTHER IMAGE");
                    Glide
                            .with(AddRequestActivity.this)
                            .load(downloadUrl.toString())
                            .into(productImage);
                    productScrollView.postDelayed(new Runnable() {
                        @Override
                        public void run(){
                            productScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }, 3000);

                }
            });
        }
    }

    private void addProduct(){

        String buyer = userEmail;
        String courier = "NONE";
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
        String url = downloadUrl.toString();

        if(!TextUtils.isEmpty(producttype) && !TextUtils.isEmpty(productname) && !TextUtils.isEmpty(length) && !TextUtils.isEmpty(width) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight) && !TextUtils.isEmpty(price)){

            //Get the unique id of the branch
            String id = databaseProducts.push().getKey();
            //Define the parameters for the database entry
            Product product = new Product(id, buyer, courier, productname, producttype, productcoords, length, width, height, weight, price, date, url);
            //Submit value to database
            databaseProducts.child(id).setValue(product);
            Toast.makeText(this, "Request added!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Loggedin.class);
            addExtras(intent);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Please ensure all fields are completed.", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, Loggedin.class);
        addExtras(intent);
        startActivity(intent);
    }

    public void addExtras(Intent intent){
        Bundle extras = new Bundle();
        extras.putString("email", userEmail);
        extras.putString("page", "buyer");
        intent.putExtras(extras);
    }
}
