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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.*;

import java.util.ArrayList;
import java.util.List;

public class MessagingFragment extends Fragment {

    public static MessagingFragment newInstance() {
        MessagingFragment fragment = new MessagingFragment();
        return fragment;
    }

    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_messaging, container, false);

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
        listViewProducts = (ListView) getView().findViewById(R.id.listViewProducts);

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                String productID = product.getProductId();
                String productName = product.getProductName();
                Intent intent = new Intent(getActivity().getApplicationContext(), ChatRoomActivity.class);
                intent.putExtra("room_name", productID);
                intent.putExtra("product_name", productName);
                intent.putExtra("user_name", userEmail.split("@", 2)[0]);
                startActivity(intent);
            }
        });
    }

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
                    String buyerID = product.getProductBuyer();
                    if (courierID.equals(userEmail) || (buyerID.equals(userEmail) && !courierID.equals("NONE"))) {
                        productList.add(product);
                    }
                }

                ProductListChat adapter = new ProductListChat(getActivity(), productList);
                listViewProducts.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
