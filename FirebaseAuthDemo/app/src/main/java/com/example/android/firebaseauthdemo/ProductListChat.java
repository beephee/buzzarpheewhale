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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.android.firebaseauthdemo.R.id.textViewProductType;

/**
 * Created by Admin on 27/5/2017.
 */

public class ProductListChat extends ArrayAdapter<Product>{

    private Activity context;
    private List<Product> productList;

    public ProductListChat(Activity context, List<Product> productList){
        super(context, R.layout.product_list_chat_layout, productList);
        this.context = context;
        this.productList= productList;
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
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewPrice);
        TextView textViewWeight = (TextView) listViewItem.findViewById(R.id.textViewWeight);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        ImageView imageViewProduct = (ImageView) listViewItem.findViewById(R.id.imageViewProduct);

        Product product = productList.get(position);

        textViewProductName.setText(product.getProductName());
        //textViewCoords.setText(product.getProductCoords().substring(0,35));
        textViewCoords.setText(product.getCountry());
        textViewBuyerEmail.setText(product.getProductBuyer().split("@", 2)[0]);
        textViewCourierEmail.setText(product.getProductCourier().split("@", 2)[0]);
        textViewPrice.setText(product.getPrice());
        textViewWeight.setText(product.getWeight());
        textViewDate.setText(product.getDate());
        Glide
                .with(context)
                .load(product.getImgurl().toString())
                .transform(new CircleTransform(context))
                .into(imageViewProduct);

        final DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(textViewProductName.getText().toString());

        return listViewItem;
    }

}
