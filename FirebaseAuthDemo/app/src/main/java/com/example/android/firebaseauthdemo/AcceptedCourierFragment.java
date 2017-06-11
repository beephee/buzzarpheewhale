package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.firebaseauthdemo.R.id.listViewProducts;

public class AcceptedCourierFragment extends Fragment {

    public static AcceptedCourierFragment newInstance() {
        AcceptedCourierFragment fragment = new AcceptedCourierFragment();
        return fragment;
    }

    DatabaseReference databaseProducts;
    ListView listViewProductsAccepted;
    List<Product> productList;
    String userEmail;
    private BottomNavigationViewEx bottomNavigationViewCourier;
    private Button suggestedOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_courier_accepted, container, false);
        Button suggestedOrders = (Button) rootView.findViewById(R.id.suggestedButton);
        suggestedOrders.setOnClickListener(suggestedListener);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");
        productList = new ArrayList<>();

        //Grabs email string from previous activity
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        listViewProductsAccepted = (ListView) getView().findViewById(R.id.listViewProductsAccepted);

        listViewProductsAccepted.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                showMenuDialog(product.getProductId(), product.getProductBuyer(), product.getProductCourier(), product.getProductName(), product.getProductType(), product.getProductCoords(), product.getLength(), product.getWidth(), product.getHeight(), product.getWeight(), product.getPrice(), product.getDate(), product.getImgurl(), product.getCountry(), product.getCourierComplete(), product.getBuyerComplete(), product.getTransit(), product.getBuyerPaid());
                return true;
            }
        });
    }

    private void showMenuDialog(final String productId, final String productBuyer, final String productCourier, final String productName, final String productType, final String productCoords, final String length, final String width, final String height, final String weight, final String price, final String date, final String url, final String country, final Boolean courierAccept, final Boolean buyerAccept, final Boolean transit, final Boolean buyerPaid) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.courier_accepted_menu, null);
        dialogBuilder.setView(dialogView);
        final Button cancelOrder = (Button) dialogView.findViewById(R.id.cancelOrderButton);
        final Button inTransit = (Button) dialogView.findViewById(R.id.transitButton);
        final Button completeTransact = (Button) dialogView.findViewById(R.id.courierCompleteTransact);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCancel(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid);
                b.dismiss();
            }
        });

        inTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTransit(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid);
                b.dismiss();
            }
        });

        completeTransact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateComplete(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid);
                b.dismiss();
            }
        });
    }

    private boolean updateCancel(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, "NONE", productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, transit, buyerPaid);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Order rejected!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean updateTransit(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, courierAccept, buyerAccept, true, buyerPaid);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Order in transit!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean updateComplete(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String url, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productCourier, productName, productType, productCoords, length, width, height, weight, price, date, url, country, true, buyerAccept, transit, buyerPaid);
        dR.setValue(product);
        Toast.makeText(getActivity().getApplicationContext(), "Transaction completed on courier's side!", Toast.LENGTH_LONG).show();
        return true;
    }

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

    @Override
    public void onStart() {
        super.onStart();

        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productList.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    // Only include items that are accepted by courier
                    String courierID = product.getProductCourier();
                    if (courierID.equals(userEmail)) {
                        productList.add(product);
                    }
                }

                ProductListAccepted adapter = new ProductListAccepted(getActivity(), productList);
                listViewProductsAccepted.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
