package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class BuyerFragment extends Fragment {

    public static BuyerFragment newInstance() {
        BuyerFragment fragment = new BuyerFragment();
        return fragment;
    }

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;
    private static final String TAG = "BuyerFragment";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String newDate;
    Spinner spinnerProductType;
    TextView textViewMyRequests;
    ImageView screenCross;
    TextView guestPrompt;
    Button btnProductCoords;
    String getCoordsResult;
    String getCountryResult;
    TextView showDatePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_buyer, container, false);

        //Twitter
        Twitter.initialize(getActivity());

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");
        productList = new ArrayList<>();

        //Grabs email string from previous activity
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        Button btnNewRequest = (Button) rootView.findViewById(R.id.buttonNewListing);
        btnNewRequest.setOnClickListener(mButtonClickListener);
        return rootView;
    }

    //Defining a class to be called by onClick does not work in Fragments
    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AddRequestActivity.class);
            intent.putExtra("email", userEmail);
            getActivity().startActivity(intent);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        listViewProducts = (ListView) getView().findViewById(R.id.listViewProducts);
        textViewMyRequests = (TextView) getView().findViewById(R.id.textViewMyRequests);
        screenCross = (ImageView) getView().findViewById(R.id.imageViewScreenCross);
        guestPrompt = (TextView) getView().findViewById(R.id.textViewGuestPrompt);

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                showMenuDialog(product.getProductId(), product.getProductBuyer(), product.getProductCourier(), product.getProductName(), product.getProductType(), product.getProductCoords(), product.getLength(), product.getWidth(), product.getHeight(), product.getWeight(), product.getPrice(), product.getDate(), product.getImgurl(), product.getCountry(), product.getCourierComplete(), product.getBuyerComplete(), product.getTransit(), product.getBuyerPaid(), product.getStatus(), product.getPaymentConfirmed(), product.getPayeeDetails(), product.getCurrency());
                return;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if(userEmail.equals("guest@dabao4me.com")){
            screenCross.setVisibility(View.VISIBLE);
            guestPrompt.setVisibility(View.VISIBLE);
        }

        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productList.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    //Filter results to show only products by the user and not yet completed
                    String email = product.getProductBuyer();
                    String status = product.getStatus();
                    if(email.equals(userEmail) && !status.equals("Completed")){
                        productList.add(product);
                    }
                }

                if(getActivity() != null) {
                    ProductListBuyer adapter = new ProductListBuyer(getActivity(), productList);
                    listViewProducts.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showMenuDialog(final String productId, final String productBuyer, final String productCourier, final String productName, final String productType, final String productCoords, final String length, final String width, final String height, final String weight, final String price, final String date, final String url, final String country, final Boolean courierAccept, final Boolean buyerAccept, final Boolean transit, final Boolean buyerPaid, final String productStatus, final Boolean paymentConfirmed, final String payeeDetails, final String currency) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.buyer_menu, null);
        dialogBuilder.setView(dialogView);

        final TextView textBankAccount = (TextView) dialogView.findViewById(R.id.editBankAccountDetails);
        textBankAccount.setText(payeeDetails);

        final Button buttonBuyerPay = (Button) dialogView.findViewById(R.id.buyerPay);
        final Button buttonBuyerCompleteTransact = (Button) dialogView.findViewById(R.id.buyerCompleteTransact);
        final Button buttonUpdateDelete = (Button) dialogView.findViewById(R.id.updateDelete);
        final ImageButton buttonPostTweet = (ImageButton) dialogView.findViewById(R.id.postTweet);
        final ShareButton buttonFbShare = (ShareButton) dialogView.findViewById(R.id.postFacebook);


        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        //hide buttons accordingly to ensure flow
        if (productStatus.equals("Pending")) {
            buttonBuyerPay.setEnabled(false);
            buttonBuyerCompleteTransact.setEnabled(false);
        } else if (productStatus.equals("Matched")) {
            buttonBuyerCompleteTransact.setEnabled(false);
        } else if (productStatus.equals("Payment Submitted")) {
            buttonBuyerPay.setEnabled(false);
            buttonBuyerCompleteTransact.setEnabled(false);
            buttonUpdateDelete.setEnabled(false);
        } else if (productStatus.equals("Payment Confirmed")) {
            buttonBuyerPay.setEnabled(false);
            buttonBuyerCompleteTransact.setEnabled(false);
            buttonUpdateDelete.setEnabled(false);
        } else if (productStatus.equals("In Transit")) {
            buttonBuyerPay.setEnabled(false);
            buttonUpdateDelete.setEnabled(false);
        }

        buttonBuyerPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productCourier.equals("NONE")) {
                    Toast.makeText(getActivity().getApplicationContext(), "No buyer yet!", Toast.LENGTH_LONG).show();
                } else {
                    confirmBuyerPay(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, true, paymentConfirmed, payeeDetails, currency);
                }
                b.dismiss();
            }
        });

        buttonBuyerCompleteTransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productCourier.equals("NONE")) {
                    Toast.makeText(getActivity().getApplicationContext(), "No buyer yet!", Toast.LENGTH_LONG).show();
                } else {
                    confirmComplete(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, true, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
                }
                b.dismiss();
            }
        });

        buttonUpdateDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDeleteDialog(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
                b.dismiss();
            }
        });

        //Twitter Sharing
        buttonPostTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                        .text("Hi friends, I'm looking for someone to buy [" + productName + "] for me from " + country + "! #dabao4me");
                builder.show();
                b.dismiss();
            }
        });

        //Facebook Sharing
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .setQuote("Hi friends, I'm looking for someone to buy [" + productName + "] for me from " + country + "! #dabao4me")
                .build();
        buttonFbShare.setShareContent(content);
    }

    private boolean confirmBuyerPay(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid, Boolean paymentConfirmed, String payeeDetails, String currency) {

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, true, paymentConfirmed, payeeDetails, currency);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Payment submitted!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean updateProduct(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid, Boolean paymentConfirmed, String payeeDetails, String currency) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Product updated!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean confirmComplete(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid, Boolean paymentConfirmed, String payeeDetails, String currency) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, true, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
        dR.setValue(product);
        // open ratings screen
        showRatingsDialog(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
        return true;
    }

    private void showRatingsDialog(final String productId, final String productBuyer, final String productCourier, final String productName, final String productType, final String productCoords, final String length, final String width, final String height, final String weight, final String price, final String date, final String url, final String country, final Boolean courierAccept, final Boolean buyerAccept, final Boolean transit, final Boolean buyerPaid, final Boolean paymentConfirmed, final String payeeDetails, final String currency) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rating_dialog, null);
        dialogBuilder.setView(dialogView);

        //need to get user UID
        //DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(productBuyer);

        final Button buttonOne = (Button) dialogView.findViewById(R.id.one);
        final Button buttonTwo = (Button) dialogView.findViewById(R.id.two);
        final Button buttonThree = (Button) dialogView.findViewById(R.id.three);
        final Button buttonFour = (Button) dialogView.findViewById(R.id.four);
        final Button buttonFive = (Button) dialogView.findViewById(R.id.five);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add one to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on buyer's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add two to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on buyer's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add three to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on buyer's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add four to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on buyer's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add five to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on buyer's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });

    }

    private void showUpdateDeleteDialog(final String productId, final String productBuyer, final String productCourier, final String productName, final String productType, final String productCoords, final String length, final String width, final String height, final String weight, final String price, final String date, final String url, final String country, final Boolean courierAccept, final Boolean buyerAccept, final Boolean transit, final Boolean buyerPaid, final Boolean paymentConfirmed, final String payeeDetails, final String currency) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextProductName = (EditText) dialogView.findViewById(R.id.editTextProductName);
        editTextProductName.setText(productName);
        spinnerProductType = (Spinner) dialogView.findViewById(R.id.spinnerProductType);
        final EditText editTextProductLength = (EditText) dialogView.findViewById(R.id.editTextProductLength);
        editTextProductLength.setText(length);
        final EditText editTextProductWidth = (EditText) dialogView.findViewById(R.id.editTextProductWidth);
        editTextProductWidth.setText(width);
        final EditText editTextProductHeight = (EditText) dialogView.findViewById(R.id.editTextProductHeight);
        editTextProductHeight.setText(height);
        final EditText editTextProductWeight = (EditText) dialogView.findViewById(R.id.editTextProductWeight);
        editTextProductWeight.setText(weight);
        final EditText editTextProductCurrency = (EditText) dialogView.findViewById(R.id.editTextProductCurrency);
        editTextProductCurrency.setText(currency);
        final EditText editTextProductPrice = (EditText) dialogView.findViewById(R.id.editTextProductPrice);
        editTextProductPrice.setText(price);
        showDatePicker = (TextView) dialogView.findViewById(R.id.editTextProductDate);
        showDatePicker.setText(date);
        btnProductCoords = (Button) dialogView.findViewById(R.id.btnProductCoords);
        btnProductCoords.setText(productCoords);
        getCoordsResult = productCoords;
        getCountryResult = country;
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateProduct);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteProduct);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        btnProductCoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPointOnMap();
            }
        });

        showDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate(true);
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = editTextProductName.getText().toString();
                String newType = spinnerProductType.getSelectedItem().toString();
                String newLength = editTextProductLength.getText().toString();
                String newWidth = editTextProductWidth.getText().toString();
                String newHeight = editTextProductHeight.getText().toString();
                String newPrice = editTextProductPrice.getText().toString();
                String newWeight = editTextProductWeight.getText().toString();
                String newCoords = getCoordsResult;
                String newCurrency = editTextProductCurrency.getText().toString();
                newDate = showDatePicker.getText().toString();
                String newCountry = getCountryResult;
                String newUrl = url; //Until we implement image uploader on update dialogue
                if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(newType) && !TextUtils.isEmpty(newLength) && !TextUtils.isEmpty(newWidth) && !TextUtils.isEmpty(newHeight) && !TextUtils.isEmpty(newWeight) && !TextUtils.isEmpty(newPrice) && !TextUtils.isEmpty(newDate) && !TextUtils.isEmpty(newCoords)) {
                    updateProduct(productId, productBuyer, productCourier, newName, newType, newCoords, newLength, newWidth, newHeight, newWeight, newPrice, newDate, newUrl, newCountry, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, newCurrency);
                    b.dismiss();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please ensure all fields are completed!", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(productId);
                b.dismiss();
            }
        });
    }

    static final int PICK_MAP_POINT_REQUEST = 999;  // The request code
    private void pickPointOnMap() {
        Intent pickPointIntent = new Intent(getContext(), MapsActivity.class);
        startActivityForResult(pickPointIntent, PICK_MAP_POINT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_MAP_POINT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                LatLng latLng = (LatLng) data.getParcelableExtra("picked_point");
                //Convert LatLng to Double, then to String and trim string length to 10
                Double dbLat = latLng.latitude;
                Double dbLng = latLng.longitude;
                String latString = dbLat.toString().substring(0,15);
                String lngString = dbLng.toString().substring(0,15);
                getCountryResult = data.getStringExtra("picked_country");
                btnProductCoords.setText(latString + "," + lngString);
                getCoordsResult = latString + "," + lngString;
                Toast.makeText(getContext(), "Location updated!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean deleteProduct(String productId) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        dR.removeValue();
        Toast.makeText(getActivity().getApplicationContext(), "Product deleted!", Toast.LENGTH_LONG).show();
        return true;
    }

    // Update date
    private void changeDate(Boolean firstCall) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if(firstCall){
            dialog.show();
            dialog.dismiss();
            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    Log.d(TAG, "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);
                    newDate = day + "/" + month + "/" + year;
                    showDatePicker.setText(newDate);
                }
            };
            changeDate(false);
        } else {
            dialog.show();
            mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    Log.d(TAG, "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);
                    newDate = day + "/" + month + "/" + year;
                    showDatePicker.setText(newDate);
                }
            };
        }
    };

}

