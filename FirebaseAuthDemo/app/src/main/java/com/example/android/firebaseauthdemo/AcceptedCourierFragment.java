package com.example.android.firebaseauthdemo;

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
                String productID = product.getProductId();
                DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productID);
                dR.child("productCourier").setValue("NONE");
                Toast.makeText(getActivity().getApplicationContext(), "Order Rejected!", Toast.LENGTH_LONG).show();
                return true;
            }
        });
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
