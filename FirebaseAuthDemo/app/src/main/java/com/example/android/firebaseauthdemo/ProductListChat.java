package com.example.android.firebaseauthdemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

/**
 * Created by Admin on 27/5/2017.
 */

public class ProductListChat extends ArrayAdapter<Product>{

    private Activity context;
    private List<Product> productList;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUsers;
    String userEmail;
    String buyerEmail;

    public ProductListChat(Activity context, List<Product> productList, String userEmail){
        super(context, R.layout.product_list_chat_layout, productList);
        this.context = context;
        this.productList = productList;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.product_list_chat_layout, null, true);

        TextView textViewProductName = (TextView) listViewItem.findViewById(R.id.textViewProductName);
        TextView textViewCoords = (TextView) listViewItem.findViewById(R.id.textViewCoords);
        TextView textViewBuyerEmail = (TextView) listViewItem.findViewById(R.id.textViewBuyerEmail);
        TextView textViewCourierEmail = (TextView) listViewItem.findViewById(R.id.textViewCourierEmail);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        CircularImageView imageViewProduct = (CircularImageView) listViewItem.findViewById(R.id.imageViewProduct);
        TextView textViewStatus = (TextView) listViewItem.findViewById(R.id.textViewStatus);
        final ImageView buyerIcon = (ImageView) listViewItem.findViewById(R.id.buyerIcon);
        final ImageView courierIcon = (ImageView) listViewItem.findViewById(R.id.courierIcon);

        Product product = productList.get(position);

        textViewProductName.setText(product.getProductName());
        textViewCoords.setText(product.getCountry());
        textViewDate.setText(product.getDate());
        textViewStatus.setText(product.getStatus());
        Glide
                .with(context)
                .load(product.getImgurl().toString())
                .transform(new CircleTransform(context))
                .into(imageViewProduct);

        buyerEmail = product.getProductBuyer();
        if(userEmail.equals(buyerEmail)){
            buyerIcon.setVisibility(View.VISIBLE);
            textViewBuyerEmail.setText("You");
            textViewCourierEmail.setText(product.getProductCourier().split("@", 2)[0]);
        } else {
            courierIcon.setVisibility(View.VISIBLE);
            textViewBuyerEmail.setText(product.getProductBuyer().split("@", 2)[0]);
            textViewCourierEmail.setText("You");
        }
        return listViewItem;
    }

}
