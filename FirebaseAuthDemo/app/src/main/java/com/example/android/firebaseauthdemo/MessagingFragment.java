package com.example.android.firebaseauthdemo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagingFragment extends Fragment {

    public static MessagingFragment newInstance() {
        MessagingFragment fragment = new MessagingFragment();
        return fragment;
    }

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;
    Button btnCustSvc;
    ImageView screenCross;
    TextView guestPrompt;

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

        //Contact Customer Service
        btnCustSvc = (Button) rootView.findViewById(R.id.btnContactStaff);
        btnCustSvc.setOnClickListener(mButtonClickListener);

        return rootView;
    }

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String productID = user.getUid();
            String productName = userEmail.split("@", 2)[0] + " - CUSTOMER SERVICE";
            Intent intent = new Intent(getActivity().getApplicationContext(), ChatRoomActivity.class);
            intent.putExtra("room_name", productID);
            intent.putExtra("product_name", productName);
            intent.putExtra("user_name", userEmail.split("@", 2)[0]);
            intent.putExtra("is_admin", "false");
            intent.putExtra("is_custsvc", "true");
            startActivity(intent);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        listViewProducts = (ListView) getView().findViewById(R.id.listViewProducts);
        screenCross = (ImageView) getView().findViewById(R.id.imageViewScreenCross);
        guestPrompt = (TextView) getView().findViewById(R.id.textViewGuestPrompt);

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
                intent.putExtra("is_admin", "false");
                intent.putExtra("is_custsvc", "false");
                startActivity(intent);
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
                    // Only include items that are accepted by courier
                    String courierID = product.getProductCourier();
                    String buyerID = product.getProductBuyer();
                    if (courierID.equals(userEmail) || (buyerID.equals(userEmail) && !courierID.equals("NONE"))) {
                        productList.add(product);
                    }
                }
                if(getActivity() != null){
                    ProductListChat adapter = new ProductListChat(getActivity(), productList, userEmail);
                    listViewProducts.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
