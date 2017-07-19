package com.example.android.firebaseauthdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductListAccepted extends ArrayAdapter<Product>{

    private Activity context;
    private List<Product> productList;
    Calendar today;
    Calendar deadline;
    RelativeLayout relativeLayout1;
    TextView textDate;
    ImageView deadlineWarning;

    public ProductListAccepted(Activity context, List<Product> productList){
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

        //Deadline Color Manage
        relativeLayout1 = (RelativeLayout)  listViewItem.findViewById(R.id.listBackground);
        textDate = (TextView) listViewItem.findViewById(R.id.textDate);
        deadlineWarning = (ImageView) listViewItem.findViewById(R.id.deadlineWarning);

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
        //ImageView imageViewProduct = (ImageView) listViewItem.findViewById(R.id.imageViewProduct);
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
        CircularImageView imageViewProduct = (CircularImageView) listViewItem.findViewById(R.id.imageViewProduct);

        Glide
                .with(context)
                .load(product.getImgurl().toString())
                .transform(new CircleTransform(context))
                .into(imageViewProduct);

        //Today
        today = Calendar.getInstance();

        //Deadline
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = formatter.parse(product.getDate());//catch exception
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        deadline = Calendar.getInstance();
        deadline.setTime(d);

        //Check for days left
        long diff = deadline.getTimeInMillis() - today.getTimeInMillis();
        long days = diff / (24 * 60 * 60 * 1000);

        if(!product.getStatus().equals("In Transit") || !product.getStatus().equals("Completed")){
            if(days < 0){
                deadlineWarning.setVisibility(View.VISIBLE);
                //relativeLayout1.setBackgroundColor(Color.parseColor("#22990000"));
                textDate.setTextColor(Color.parseColor("#FF0000"));
                textViewDate.setTextColor(Color.parseColor("#FF0000"));
                String htmlText = product.getProductName() + "<br><font color=#FF0000>(EXPIRED)</font>";
                if (Build.VERSION.SDK_INT >= 24) {
                    textViewProductName.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textViewProductName.setText(Html.fromHtml(htmlText));
                }
            } else if(days < 7){
                deadlineWarning.setVisibility(View.VISIBLE);
                //relativeLayout1.setBackgroundColor(Color.parseColor("#22990000"));
                textDate.setTextColor(Color.parseColor("#f76009"));
                textViewDate.setTextColor(Color.parseColor("#f76009"));
                String daysText = "days";
                if(days == 1) {
                    daysText = "day";
                }
                String htmlText = product.getProductName() + "<br><font color=#f76009>(" + days + " " + daysText + " left)</font>";
                if (Build.VERSION.SDK_INT >= 24) {
                    textViewProductName.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textViewProductName.setText(Html.fromHtml(htmlText));
                }
            }
        }

        final DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(textViewProductName.getText().toString());

        return listViewItem;
    }

}
