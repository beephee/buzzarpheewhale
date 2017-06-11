package com.example.android.firebaseauthdemo;

/**
 * Created by Admin on 25/5/2017.
 */

public class Product {

    String productId;
    String productBuyer;
    String productCourier;
    String productName;
    String productType;
    String productCoords;
    String length;
    String width;
    String height;
    String weight;
    String price;
    String date;
    String imgurl;
    String country;
    Boolean courierComplete;
    Boolean buyerComplete;
    Boolean transit;
    Boolean buyerPaid;

    public Product(){

    }

    public Product(String productId, String productBuyer, String productCourier, String productName, String productType, String productCoords, String length, String width, String height, String weight, String price, String date, String imgurl, String country, Boolean courierAccept, Boolean buyerAccept, Boolean transit, Boolean buyerPaid) {
        this.productId = productId;
        this.productBuyer = productBuyer;
        this.productCourier = productCourier;
        this.productName = productName;
        this.productType = productType;
        this.productCoords = productCoords;
        this.length = length;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.price = price;
        this.date = date;
        this.imgurl = imgurl;
        this.country = country;
        this.courierComplete = courierAccept;
        this.buyerComplete = buyerAccept;
        this.transit = transit;
        this.buyerPaid = buyerPaid;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductBuyer() {
        return productBuyer;
    }

    public String getProductCourier() {
        return productCourier;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public String getProductCoords() {
        return productCoords;
    }

    public String getLength() {
        return length;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getImgurl() { return imgurl; }

    public String getCountry() { return country; }

    public Boolean getCourierComplete() { return courierComplete; }

    public Boolean getBuyerComplete() { return buyerComplete; }

    public Boolean getTransit() { return transit; }

    public String getStatus() {
        if (this.getProductCourier().equals("NONE")) {
            return "Pending";
        } else if (this.getBuyerComplete().equals(true) && this.getCourierComplete().equals(true)) {
            return "Completed";
        } else if (this.getBuyerPaid().equals(true)) {
            return "Payment Completed";
        } else if (this.getTransit().equals(true)) {
            return "In Transit";
        } else {
            return "Matched";
        }
    }

    public Boolean getBuyerPaid() { return buyerPaid; }
}
