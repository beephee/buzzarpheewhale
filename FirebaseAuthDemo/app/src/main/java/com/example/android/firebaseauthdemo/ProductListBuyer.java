package com.example.android.firebaseauthdemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static android.R.id.list;

/**
 * Created by Admin on 27/5/2017.
 */

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

        Product product = productList.get(position);

        textViewProductName.setText(product.getProductName());
        textViewProductType.setText(product.getProductType());
        textViewCoords.setText(product.getProductCoords());
        textViewBuyerEmail.setText(product.getProductBuyer());
        textViewPrice.setText(product.getPrice());
        textViewWeight.setText(product.getWeight());
        textViewHeight.setText(product.getHeight());
        textViewWidth.setText(product.getWidth());
        textViewLength.setText(product.getLength());

        Button deleteBtn = (Button) listViewItem.findViewById(R.id.buttonDelete);
        final DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(textViewProductName.getText().toString());

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /* <Insert here an actual method to remove object from Firebase */
                dR.removeValue(); // Server-side Removal: This ain't working
                productList.remove(position); //Local Removal: This works but redundant
                notifyDataSetChanged();
            }
        });

        return listViewItem;
    }

}
