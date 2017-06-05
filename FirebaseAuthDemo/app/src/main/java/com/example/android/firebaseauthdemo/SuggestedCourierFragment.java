package com.example.android.firebaseauthdemo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.*;

import java.util.ArrayList;
import java.util.List;

public class SuggestedCourierFragment extends Fragment {

    public static SuggestedCourierFragment newInstance() {
        SuggestedCourierFragment fragment = new SuggestedCourierFragment();
        return fragment;
    }

    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;
    private BottomNavigationViewEx bottomNavigationViewCourier;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_courier_suggested, container, false);
        Button acceptedOrders = (Button) rootView.findViewById(R.id.acceptedButton);
        acceptedOrders.setOnClickListener(acceptedListener);

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
    }

    private View.OnClickListener acceptedListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Create new fragment and transaction
            Fragment nextFrag = new AcceptedCourierFragment();
            FragmentTransaction transact = getFragmentManager().beginTransaction();
            transact.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transact.replace(R.id.frame_layout, nextFrag);
            transact.addToBackStack(null);

            // Commit the transaction
            transact.commit();
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
                    // Only include items that are not assigned to any courier yet and not requested by user
                    String courierID = product.getProductCourier();
                    String buyerID = product.getProductBuyer();
                    if (courierID.equals("NONE") && !buyerID.equals(userEmail)) {
                        productList.add(product);
                    }
                }

                ProductList adapter = new ProductList(getActivity(), productList);
                listViewProducts.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
