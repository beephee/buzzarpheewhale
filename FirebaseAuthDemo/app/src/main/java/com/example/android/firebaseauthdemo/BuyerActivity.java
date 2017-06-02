package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class BuyerActivity extends Fragment {

    public static BuyerActivity newInstance() {
        BuyerActivity fragment = new BuyerActivity();
        return fragment;
    }

    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;
    private static final String TAG = "BuyerActivity";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date;
    String newDate;
    String getCoords;
    Spinner spinnerProductType;
    private BottomNavigationViewEx bottomNavigationViewBuyer;
    TextView textViewMyRequests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_buyer, container, false);

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

        listViewProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                showUpdateDeleteDialog(product.getProductId(), product.getProductBuyerEmail(), product.getProductCourier(), product.getProductName(), product.getProductType(), product.getProductCoords(), product.getLength(), product.getWidth(), product.getHeight(), product.getWeight(), product.getPrice(), product.getDate(), product.getImgurl());
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //textViewMyRequests.setText(userEmail2); //For debugging if email was passed
        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productList.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    //Filter results to show only products by the user
                    String email = product.getProductBuyerEmail();
                    if(email.equals(userEmail)){
                        productList.add(product);
                    }
                }

                ProductListBuyer adapter = new ProductListBuyer(getActivity(), productList);
                listViewProducts.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean updateProduct(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Product Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private void showUpdateDeleteDialog(final String productId, final String productBuyer, final String productCourier, final String productName, final String productType, final String productCoords, final String length, final String width, final String height, final String weight, final String price, final String date, final String url) {

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
        final EditText editTextProductPrice = (EditText) dialogView.findViewById(R.id.editTextProductPrice);
        editTextProductPrice.setText(price);
        final TextView showDatePicker = (TextView) dialogView.findViewById(R.id.editTextProductDate);
        showDatePicker.setText(date);
        final EditText editTextProductCoords = (EditText) dialogView.findViewById(R.id.editTextProductCoords);
        editTextProductCoords.setText(productCoords);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateProduct);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteProduct);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        showDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDate();
                showDatePicker.setText(newDate);
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
                String newCoords = editTextProductCoords.getText().toString();
                String newUrl = url; //Until we implement image uploader on update dialogue
                if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(newType) && !TextUtils.isEmpty(newLength) && !TextUtils.isEmpty(newWidth) && !TextUtils.isEmpty(newHeight) && !TextUtils.isEmpty(newWeight) && !TextUtils.isEmpty(newPrice) && !TextUtils.isEmpty(newDate) && !TextUtils.isEmpty(newCoords)) {
                    updateProduct(productId, productBuyer, productCourier, newName, newType, newCoords, newLength, newWidth, newHeight, newWeight, newPrice, newDate, newUrl);
                    b.dismiss();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please ensure all fields are completed.", Toast.LENGTH_LONG).show();
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

    private boolean deleteProduct(String productId) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        dR.removeValue();
        Toast.makeText(getActivity().getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();
        return true;
    }

    // Update date
    private void changeDate() {
        final TextView showDatePicker = (TextView) getView().findViewById(R.id.dateValue);
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
        dialog.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                newDate = day + "/" + month + "/" + year;
            }
        };
    };

}

