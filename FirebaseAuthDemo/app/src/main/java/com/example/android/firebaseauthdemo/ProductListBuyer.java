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

public class ProductListBuyer extends ArrayAdapter<Product>{

    private Activity context;
    private List<Product> productList;

    public ProductListBuyer(Activity context, List<Product> productList){
        super(context, R.layout.product_list_buyer_layout, productList);
        this.context = context;
        this.productList= productList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.product_list_buyer_layout, null, true);

        TextView textViewProductName = (TextView) listViewItem.findViewById(R.id.textViewProductName);
        TextView textViewProductType = (TextView) listViewItem.findViewById(R.id.textViewProductType);
        TextView textViewCoords = (TextView) listViewItem.findViewById(R.id.textViewCoords);
        TextView textViewBuyerEmail = (TextView) listViewItem.findViewById(R.id.textViewBuyerEmail);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.textViewPrice);
        TextView textViewWeight = (TextView) listViewItem.findViewById(R.id.textViewWeight);
        TextView textViewHeight = (TextView) listViewItem.findViewById(R.id.textViewHeight);
        TextView textViewWidth = (TextView) listViewItem.findViewById(R.id.textViewWidth);
        TextView textViewLength = (TextView) listViewItem.findViewById(R.id.textViewLength);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        ImageView imageViewProduct = (ImageView) listViewItem.findViewById(R.id.imageViewProduct);
        TextView textViewStatus = (TextView) listViewItem.findViewById(R.id.textViewStatus);

        Product product = productList.get(position);

        textViewProductName.setText(product.getProductName());
        textViewProductType.setText(product.getProductType());
        //textViewCoords.setText(product.getProductCoords().substring(0,35));
        textViewCoords.setText(product.getCountry());
        textViewBuyerEmail.setText(product.getProductBuyer().split("@", 2)[0]);
        textViewPrice.setText(product.getPrice() + " " + product.getCurrency());
        textViewWeight.setText(product.getWeight() + " kg");
        textViewHeight.setText(product.getHeight());
        textViewWidth.setText(product.getWidth());
        textViewLength.setText(product.getLength() + " cm");
        textViewDate.setText(product.getDate());
        textViewStatus.setText(product.getStatus());

        Glide
                .with(context)
                .load(product.getImgurl().toString())
                .transform(new CircleTransform(context))
                .into(imageViewProduct);

        final DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(textViewProductName.getText().toString());

        return listViewItem;
    }

}
