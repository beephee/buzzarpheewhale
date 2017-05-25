package com.example.android.firebaseauthdemo;

/**
 * Created by Admin on 25/5/2017.
 */

public class Product {

    String productId;
    String productBuyer;
    String productType;

    public Product(){

    }

    public Product(String productId, String productBuyer, String productType) {
        this.productId = productId;
        this.productBuyer = productBuyer;
        this.productType = productType;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductBuyer() {
        return productBuyer;
    }

    public String getProductType() {
        return productType;
    }
}
