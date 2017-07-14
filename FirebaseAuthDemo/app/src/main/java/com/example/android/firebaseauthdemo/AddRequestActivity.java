package com.example.android.firebaseauthdemo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import static com.example.android.firebaseauthdemo.R.id.dateValue;

public class AddRequestActivity extends AppCompatActivity {

    Button buttonNewListing;
    Button buttonGetCoordinates;
    Spinner spinnerProductType;
    FirebaseAuth firebaseAuth;
    String buyerCountry;
    String customsURL;

    //Image Storage Variables
    DatabaseReference databaseProducts;
    StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    Uri downloadUrl;
    CircularImageView productImage;

    //Product details to be stored
    String country;
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

        firebaseAuth = FirebaseAuth.getInstance();
        buttonGetCoordinates = (Button) findViewById(R.id.buttonGetCoordinates);
        buttonGetCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPointOnMap();
            }
        });

        //User Info
        DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buyerCountry = dataSnapshot.child("buyerCountry").getValue(String.class);
                switch (buyerCountry){
                    case "Australia" :
                        customsURL = "https://www.border.gov.au/Trav/Impo/Proh";
                        break;
                    case "China" :
                        customsURL = "http://english.customs.gov.cn/service/guide?c=e6fb7440-5df2-4e87-bf92-c4eb697324d8&k=45";
                        break;
                    case "Japan" :
                        customsURL = "http://www.customs.go.jp/english/summary/prohibit.htm";
                        break;
                    case "Malaysia" :
                        customsURL = "http://www.customs.gov.my/en/tp/pages/tp_ie.aspx";
                        break;
                    case "Singapore" :
                        customsURL = "https://www.customs.gov.sg/businesses/importing-goods/controlled-and-prohibited-goods-for-import";
                        break;
                    case "United Kingdom" :
                        customsURL = "https://www.gov.uk/duty-free-goods/banned-and-restricted-goods";
                        break;
                    case "United States" :
                        customsURL = "https://www.cbp.gov/travel/us-citizens/know-before-you-go/prohibited-and-restricted-items";
                        break;
                    default :
                        customsURL = "Unable to find " + buyerCountry + "'s customs page";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Values will be submitted to the "products" node in the JSON tree
        databaseProducts = FirebaseDatabase.getInstance().getReference("products");

        buttonNewListing = (Button) findViewById(R.id.buttonNewListing);
        buttonNewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(userEmail.equals("guest@dabao4me.com")) {
                    showGuestDialog();
                } else {
                    addProduct(false);
                }
            }
        });

        spinnerProductType = (Spinner) findViewById(R.id.spinnerProductType);

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
        productImage = (CircularImageView) findViewById(R.id.imageViewProduct);
        mProgressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(userEmail.equals("guest@dabao4me.com")){
                    showGuestDialog();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_INTENT);
                }
            }
        });

    }

    public void showGuestDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.guest_menu, null);
        dialogBuilder.setView(dialogView);

        final Button btnBack = (Button) dialogView.findViewById(R.id.btnBack);
        final Button btnLogin = (Button) dialogView.findViewById(R.id.btnLogin);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(AddRequestActivity.this, LoginActivity.class));
                b.dismiss();
            }
        });

    }

    public void showCustomsDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.customs_prompt, null);
        dialogBuilder.setView(dialogView);

        final Button btnBack = (Button) dialogView.findViewById(R.id.btnBack);
        final Button btnConfirm = (Button) dialogView.findViewById(R.id.btnConfirm);
        final TextView userCountry = (TextView) dialogView.findViewById(R.id.textViewUserCountry);
        final TextView customsLink = (TextView) dialogView.findViewById(R.id.textViewCustomsURL);
        userCountry.setText(buyerCountry);
        customsLink.setText(customsURL);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct(true);
                b.dismiss();
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
                //Convert LatLng to Double, then to String and trim string length to 10
                Double dbLat = latLng.latitude;
                Double dbLng = latLng.longitude;
                String latString = dbLat.toString().substring(0,15);
                String lngString = dbLng.toString().substring(0,15);
                country = (String) data.getStringExtra("picked_country");
                Toast.makeText(this, "Product is located in: " + country, Toast.LENGTH_LONG).show();
                buttonGetCoordinates.setText(country + "\n(" + latString + "," + lngString + ")");
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
                    Glide
                            .with(AddRequestActivity.this)
                            .load(downloadUrl.toString())
                            .into(productImage);
                }
            });
        }
    }

    private void addProduct(Boolean customAccepted){

        String buyer = userEmail;
        String courier = "NONE";
        Boolean courierAccept = Boolean.FALSE;
        Boolean buyerAccept = Boolean.FALSE;
        Boolean transit = Boolean.FALSE;
        Boolean buyerPaid = Boolean.FALSE;
        Boolean paymentConfirmed = Boolean.FALSE;
        String payeeDetails = "Not Applicable";
        String producttype = spinnerProductType.getSelectedItem().toString();
        EditText editProductName = (EditText) findViewById(R.id.editTextProductName);
        String productname = editProductName.getText().toString();
        EditText editCurrency = (EditText) findViewById(R.id.currencyValue);
        String currency = editCurrency.getText().toString();
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
        String url = "NONE";
        if(downloadUrl != null){
            url = downloadUrl.toString();
        }

        if(!TextUtils.isEmpty(producttype) && !TextUtils.isEmpty(productname) && !TextUtils.isEmpty(length) && !TextUtils.isEmpty(currency) && !TextUtils.isEmpty(width) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty((url))){

            if(customAccepted) {
                //Get the unique id of the branch
                String id = databaseProducts.push().getKey();
                //Define the parameters for the database entry
                Product product = new Product(id, buyer, courier, productname, producttype, productcoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
                //Submit value to database
                databaseProducts.child(id).setValue(product);
                Toast.makeText(this, "Request added!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoggedinActivity.class);
                addExtras(intent);
                startActivity(intent);
            } else {
                showCustomsDialog();
            }
        }else{
            Toast.makeText(this, "Please ensure all fields are completed.", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(this, LoggedinActivity.class);
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
