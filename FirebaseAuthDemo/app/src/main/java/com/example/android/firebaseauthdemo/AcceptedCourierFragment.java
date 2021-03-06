package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

public class AcceptedCourierFragment extends Fragment {

    public static AcceptedCourierFragment newInstance() {
        AcceptedCourierFragment fragment = new AcceptedCourierFragment();
        return fragment;
    }

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseUsers;
    DatabaseReference databaseProductsAccepted;
    DatabaseReference databaseProductsAll;
    ListView listViewProductsAccepted;
    List<Product> productListAccepted;
    ListView listViewProductsAll;
    List<Product> productListAll;
    ProductList adapterAll;
    String userEmail;
    String listFilter;
    String userCountry;
    Boolean userCourierActive;
    String userCourierCountry;
    String userDate;
    String userWeight;
    String userBankAccount;
    Button btnAccepted;
    Button btnSuggested;
    Button btnAll;
    ImageView screenCross;
    TextView guestPrompt;
    Boolean allView = false;
    String currencyCode;
    String exchangeURL;
    Double data = 0.0;
    RequestQueue requestQueue;
    Calendar today;
    Calendar deadline;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_courier_accepted, container, false);
        //To remove once filter tabs are finalized
        Button suggestedOrders = (Button) rootView.findViewById(R.id.suggestedButton);
        suggestedOrders.setOnClickListener(suggestedListener);

        mContext = getActivity();
        databaseProductsAccepted = FirebaseDatabase.getInstance().getReference("products");
        databaseProductsAll = FirebaseDatabase.getInstance().getReference("products");
        productListAccepted = new ArrayList<>();
        productListAll = new ArrayList<>();
        listFilter = "suggested";

        //Filter Buttons
        btnAccepted = (Button) rootView.findViewById(R.id.btnAccepted);
        btnSuggested = (Button) rootView.findViewById(R.id.btnSuggested);
        btnAll = (Button) rootView.findViewById(R.id.btnAll);
        btnAccepted.setOnClickListener(btnAcceptedListener);
        btnSuggested.setOnClickListener(btnSuggestedListener);
        btnAll.setOnClickListener(btnAllListener);
        btnAccepted.setTypeface(Typeface.DEFAULT_BOLD);
        btnAccepted.setTextSize(16);

        //User Info
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userCountry = dataSnapshot.child("buyerCountry").getValue(String.class);
                userCourierActive = dataSnapshot.child("courierActive").getValue(Boolean.class);
                userCourierCountry = dataSnapshot.child("courierCountry").getValue(String.class);
                userDate = dataSnapshot.child("dateDeparture").getValue(String.class);
                userWeight = dataSnapshot.child("maxWeight").getValue(String.class);
                userBankAccount = dataSnapshot.child("bankAccount").getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Accepted Tab
        databaseProductsAccepted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productListAccepted.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    // Only include items that are accepted by courier and not yet completed
                    String courierID = product.getProductCourier();
                    String status = product.getStatus();
                    if (courierID.equals(userEmail) && !status.equals("Completed")) {
                        productListAccepted.add(product);
                    }
                }

                if(getActivity() != null){
                    ProductListAccepted adapter = new ProductListAccepted(getActivity(), productListAccepted);
                    listViewProductsAccepted.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Grabs email string from previous activity
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }
        return rootView;
    }

    //Accepted Filter Button
    private View.OnClickListener btnAcceptedListener = new View.OnClickListener() {
        public void onClick(View v) {
            allView = false;
            if(userEmail.equals("guest@dabao4me.com")){
                screenCross.setVisibility(View.VISIBLE);
                guestPrompt.setVisibility(View.VISIBLE);
            }
            listViewProductsAccepted.setVisibility(View.VISIBLE);
            listViewProductsAll.setVisibility(View.INVISIBLE);
            clearButtonStyle();
            btnAccepted.setTypeface(Typeface.DEFAULT_BOLD);
            btnAccepted.setTextSize(16);
        }
    };

    //Suggested Filter Button
    private View.OnClickListener btnSuggestedListener = new View.OnClickListener() {
        public void onClick(View v) {
            allView = false;
            if(userEmail.equals("guest@dabao4me.com")){
                screenCross.setVisibility(View.VISIBLE);
                guestPrompt.setVisibility(View.VISIBLE);
            }
            // hide if courier not active or courier country not set
            if (userCourierActive == false || userCourierCountry.equals("NONE")) {
                listFilter = "none";
                Toast.makeText(getActivity().getApplicationContext(), "Set courier status to active and set courier country to see suggested orders!", Toast.LENGTH_LONG).show();
            } else {
                listFilter = "suggested";
            }
            listViewProductsAccepted.setVisibility(View.INVISIBLE);
            listViewProductsAll.setVisibility(View.VISIBLE);
            onStart();
            adapterAll.notifyDataSetChanged();
            clearButtonStyle();
            btnSuggested.setTypeface(Typeface.DEFAULT_BOLD);
            btnSuggested.setTextSize(16);
        }
    };

    //Display All Button
    private View.OnClickListener btnAllListener = new View.OnClickListener() {
        public void onClick(View v) {
            allView = true;
            screenCross.setVisibility(View.INVISIBLE);
            guestPrompt.setVisibility(View.INVISIBLE);
            listFilter = "all";
            listViewProductsAccepted.setVisibility(View.INVISIBLE);
            listViewProductsAll.setVisibility(View.VISIBLE);
            onStart();
            adapterAll.notifyDataSetChanged();
            clearButtonStyle();
            btnAll.setTypeface(Typeface.DEFAULT_BOLD);
            btnAll.setTextSize(16);
        }
    };

    //Reset Button Styles
    private void clearButtonStyle(){
        Button btnAccepted = (Button) getView().findViewById(R.id.btnAccepted);
        btnAccepted.setTypeface(Typeface.SANS_SERIF);
        btnAccepted.setTextSize(14);
        Button btnSuggested = (Button) getView().findViewById(R.id.btnSuggested);
        btnSuggested.setTypeface(Typeface.SANS_SERIF);
        btnSuggested.setTextSize(14);
        Button btnAll = (Button) getView().findViewById(R.id.btnAll);
        btnAll.setTypeface(Typeface.SANS_SERIF);
        btnAll.setTextSize(14);
    }

    public void showGuestDialogFragment() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                startActivity(new Intent(getActivity(), LoginActivity.class));
                b.dismiss();
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        listViewProductsAccepted = (ListView) getView().findViewById(R.id.listViewProductsAccepted);
        listViewProductsAll = (ListView) getView().findViewById(R.id.listViewProductsAll);
        screenCross = (ImageView) getView().findViewById(R.id.imageViewScreenCross);
        guestPrompt = (TextView) getView().findViewById(R.id.textViewGuestPrompt);

        listViewProductsAccepted.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productListAccepted.get(i);
                showMenuDialog(product.getProductId(), product.getProductBuyer(), product.getProductCourier(), product.getProductName(), product.getProductType(), product.getProductCoords(), product.getLength(), product.getWidth(), product.getHeight(), product.getWeight(), product.getPrice(), product.getDate(), product.getImgurl(), product.getCountry(), product.getCourierComplete(), product.getBuyerComplete(), product.getTransit(), product.getBuyerPaid(), product.getStatus(), product.getPaymentConfirmed(), product.getPayeeDetails(), product.getCurrency());
                return;
            }
        });

        listViewProductsAll.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (userEmail.equals("guest@dabao4me.com")) {
                    showGuestDialogFragment();
                }
                else if (userBankAccount.equals("Not Applicable")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please set bank account details first!", Toast.LENGTH_LONG).show();
                } else {
                    Product product = productListAll.get(i);
                    String productID = product.getProductId();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date d = null;
                    try {
                        d = formatter.parse(product.getDate());//catch exception
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    deadline = Calendar.getInstance();
                    deadline.setTime(d);
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productID);
                    dR.child("productCourier").setValue(userEmail);
                    dR.child("payeeDetails").setValue(userBankAccount);
                    Toast.makeText(getActivity().getApplicationContext(), "Order accepted!", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    //Menu Dialog for Accepted Tab
    private void showMenuDialog(final String productId, final String productBuyer, final String productCourier, final String productName, final String productType, final String productCoords, final String length, final String width, final String height, final String weight, final String price, final String date, final String url, final String country, final Boolean courierAccept, final Boolean buyerAccept, final Boolean transit, final Boolean buyerPaid, final String productStatus, final Boolean paymentConfirmed, final String payeeDetails, final String currency) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.courier_accepted_menu, null);
        dialogBuilder.setView(dialogView);

        final TextView originalPrice = (TextView) dialogView.findViewById(R.id.originalPrice);
        originalPrice.setText(price + " " + currency);

        switch (userCountry){
            case "Australia" :
                currencyCode = "AUD";
                break;
            case "China" :
                currencyCode = "RMB";
                break;
            case "Japan" :
                currencyCode = "JPY";
                break;
            case "Malaysia" :
                currencyCode = "MYR";
                break;
            case "Singapore" :
                currencyCode = "SGD";
                break;
            case "United Kingdom" :
                currencyCode = "GBP";
                break;
            case "United States" :
                currencyCode = "USD";
                break;
        }

        final TextView exchangePrice = (TextView) dialogView.findViewById(R.id.exchangePrice);

        if (currency.equals(currencyCode)) {
            exchangePrice.setText(price + " " + currency);
        } else {
            exchangeURL = "http://api.fixer.io/latest?base=" + currency;

            requestQueue = Volley.newRequestQueue(this.getActivity());
            // Creating the JsonObjectRequest class called obreq, passing required parameters:
            // GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
            JsonObjectRequest obreq = new JsonObjectRequest(exchangeURL, null,
                    // The third parameter Listener overrides the method onResponse() and passes
                    //JSONObject as a parameter
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = response.getJSONObject("rates");
                                // Retrieves the string labeled "colorName" and "description" from
                                // the response JSON Object
                                // and converts them into javascript objects
                                Double newCurrency = obj.getDouble(currencyCode);

                                data = newCurrency;
                            }
                            // Try and catch are included to handle any errors due to JSON
                            catch (JSONException e) {
                                // If an error occurs, this prints the error to the log
                                e.printStackTrace();
                            }
                        }
                    },
                    // The final parameter overrides the method onErrorResponse() and passes VolleyError
                    //as a parameter
                    new Response.ErrorListener() {
                        @Override
                        // Handles errors that occur due to Volley
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                        }
                    }
            );

            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);
            Double oldPrice = Double.parseDouble(price);
            exchangePrice.setText(String.format("%.2f", oldPrice * data) + " " + currencyCode);
        }

        final Button cancelOrder = (Button) dialogView.findViewById(R.id.cancelOrderButton);
        final Button confirmPayment = (Button) dialogView.findViewById(R.id.confirmPaymentButton);
        final Button inTransit = (Button) dialogView.findViewById(R.id.transitButton);
        final Button completeTransact = (Button) dialogView.findViewById(R.id.courierCompleteTransact);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        //hide buttons accordingly to ensure flow
        if (productStatus.equals("Matched")) {
            inTransit.setEnabled(false);
            confirmPayment.setEnabled(false);
            completeTransact.setEnabled(false);
        } else if (productStatus.equals("Payment Submitted")) {
            cancelOrder.setEnabled(false);
            inTransit.setEnabled(false);
            completeTransact.setEnabled(false);
        } else if (productStatus.equals("Payment Confirmed")) {
            cancelOrder.setEnabled(false);
            confirmPayment.setEnabled(false);
            completeTransact.setEnabled(false);
        } else if (productStatus.equals("In Transit")) {
            cancelOrder.setEnabled(false);
            confirmPayment.setEnabled(false);
            inTransit.setEnabled(false);
        }

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCancel(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
                b.dismiss();
            }
        });

        confirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateConfirm(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
                b.dismiss();
            }
        });

        inTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTransit(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
                b.dismiss();
            }
        });

        completeTransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateComplete(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
                b.dismiss();
            }
        });
    }

    private boolean updateCancel(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid, Boolean paymentConfirmed, String payeeDetails, String currency) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, "NONE", productName, productType, productCoords, length, width, height, weight, price, date, url, country, false, false, false, false, false, "Not Applicable", currency); // reset values to false
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Order rejected!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean updateConfirm(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid, Boolean paymentConfirmed, String payeeDetails, String currency) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid, true, payeeDetails, currency);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Payment confirmed!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean updateTransit(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid, Boolean paymentConfirmed, String payeeDetails, String currency) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, true, buyerPaid, paymentConfirmed, payeeDetails, currency);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Order in transit!", Toast.LENGTH_LONG).show();
        return true;
    }
    
    private boolean updateComplete(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid, Boolean paymentConfirmed, String payeeDetails, String currency) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, true, buyerAccept, transit, buyerPaid, paymentConfirmed, payeeDetails, currency);
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
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on courier's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add two to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on courier's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add three to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on courier's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add four to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on courier's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });
        buttonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add one to numRatings
                //add five to totalScore
                Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on courier's side!", Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        });

    }

    //Can remove once filter tabs are finalized
    private View.OnClickListener suggestedListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Create new fragment and transaction
            Fragment newFrag = new SuggestedCourierFragment();
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            trans.replace(R.id.frame_layout, newFrag);
            trans.addToBackStack(null);

            // Commit the transaction
            trans.commit();
        }
    };

    public static boolean checkDates(String startDate, String endDate) {

        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yyyy");

        boolean b = false;

        try {
            if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                b = true;  // If start date is before end date.
            } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                b = true;  // If two dates are equal.
            } else {
                b = false; // If start date is after the end date.
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return b;
    }

    public static boolean checkWeight(String firstWeight, String secondWeight) {

        boolean b = false;
        double weightOne = Double.parseDouble(firstWeight);
        double weightTwo = Double.parseDouble(secondWeight);

        try {
            if (weightOne < weightTwo) {
                b = true;  // If first weight is less than second weight
            } else {
                b = false; // If first weight is more than second weight
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return b;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(userEmail.equals("guest@dabao4me.com") && !allView){
            screenCross.setVisibility(View.VISIBLE);
            guestPrompt.setVisibility(View.VISIBLE);
        }

        //Suggested & Display All Tab
        databaseProductsAll.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productListAll.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);

                    String courierID = product.getProductCourier();
                    String buyerID = product.getProductBuyer();
                    String requiredDate = product.getDate();
                    String productWeight = product.getWeight();

                    // Only include items that are not assigned to any courier yet and not requested by user
                    if (courierID.equals("NONE") && !buyerID.equals(userEmail)) {
                        switch (listFilter) {
                            //Only add requests in same country as user, user departure date must be before required date, user max weight must be than more product weight
                            case "suggested":
                                if(userCourierCountry != null) {
                                    if (userCourierCountry.equals(product.getCountry()) && checkDates(userDate,requiredDate) && checkWeight(productWeight,userWeight)) {
                                        productListAll.add(product);
                                    }
                                }
                                break;
                            //List all requests
                            case "all":
                                productListAll.add(product);
                                break;
                            //List none
                            case "none":
                                break;
                        }
                    }
                }
                if(getActivity() != null){
                    adapterAll = new ProductList(getActivity(), productListAll);
                    listViewProductsAll.setAdapter(adapterAll);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
