package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.name;
import static android.media.CamcorderProfile.get;
import static com.example.android.firebaseauthdemo.R.id.editTextProductName;
import static com.example.android.firebaseauthdemo.R.id.editTextProductName;

public class BuyerActivity extends AppCompatActivity {

    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");

        listViewProducts = (ListView) findViewById(R.id.listViewProducts);

        listViewProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                showUpdateDeleteDialog(product.getProductId(), product.getProductBuyer(), product.getProductName(), product.getProductType(), product.getProductCoords(), product.getLength(), product.getWidth(), product.getHeight(), product.getWeight(), product.getPrice(), product.getDate());
                return true;
            }
        });

        productList = new ArrayList<>();

        //Grabs email string from previous activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                productList.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    //Filter results to show only products by the user
                    String email = product.getProductBuyer();
                    if(email.equals(userEmail)){
                        productList.add(product);
                    }
                }

                ProductListBuyer adapter = new ProductListBuyer(BuyerActivity.this, productList);
                listViewProducts.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean updateProduct(String productId, String productBuyer, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product product = new Product(productId, productBuyer, productName, productType, productCoords, length, width, height, weight, price, date);
        dR.setValue(product);
        Toast.makeText(getApplicationContext(), "Product Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private void showUpdateDeleteDialog(final String productId, final String productBuyer, final String productName, final String productType, final String productCoords, final String length, final String width, final String height, final String weight, final String price, final String date) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextProductName = (EditText) dialogView.findViewById(R.id.editTextProductName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateProduct);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteProduct);

        dialogBuilder.setTitle(productName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextProductName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    updateProduct(productId, productBuyer, name, productType, productCoords, length, width, height, weight, price, date);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(productId);
                b.dismiss();
            }
        });
    }

    private boolean deleteProduct(String productId) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("products").child(productId);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();
        return true;
    }

    //Goto Courier Dashboard
    public void viewAddRequest(View view)
    {
        Intent intent = new Intent(this, AddRequestActivity.class);
        //Pass the email string to next activity
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

    public void goCourierPage(View view)
    {
        Intent intent = new Intent(this, CourierActivity.class);
        //Pass the email string to next activity
        intent.putExtra("email", userEmail);
        startActivity(intent);
    }

}
