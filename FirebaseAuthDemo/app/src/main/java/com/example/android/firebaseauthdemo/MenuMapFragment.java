package com.example.android.firebaseauthdemo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class MenuMapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    String userEmail;
    DatabaseReference databaseProducts;
    ArrayList<LatLng> LocList;
    ArrayList<String> ImgList;
    ArrayList<String> ProductNameList;
    ArrayList<String> PriceList;
    ArrayList<String> ServiceTypeList;
    ArrayList<String> CountryList;
    ArrayList<String> CategoryList;
    ArrayList<String> DateList;
    ArrayList<String> StatusList;
    int arrSize;
    int curItemIndex = 0;
    ImageView imageViewProduct;
    TextView textViewProductName;
    TextView textViewPrice;
    TextView textViewServiceType;
    TextView textViewCountry;
    TextView textViewCategory;
    TextView textViewDeadline;
    TextView textViewStatus;

    public static MenuMapFragment newInstance() {
        MenuMapFragment fragment = new MenuMapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_menu_map, container, false);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");
        LocList = new ArrayList();
        ImgList = new ArrayList();
        ProductNameList = new ArrayList();
        PriceList = new ArrayList();
        ServiceTypeList = new ArrayList();
        CountryList = new ArrayList();
        CategoryList = new ArrayList();
        DateList = new ArrayList();
        StatusList = new ArrayList();

        Button btnPrev = (Button) rootView.findViewById(R.id.prevItem);
        btnPrev.setOnClickListener(ButtonPrevClickListener);
        Button btnNext = (Button) rootView.findViewById(R.id.nextItem);
        btnNext.setOnClickListener(ButtonNextClickListener);

        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        //Bottom Product Info Panel
        imageViewProduct = (ImageView) rootView.findViewById(R.id.imageViewProduct);
        textViewProductName = (TextView) rootView.findViewById(R.id.textViewProductName);
        textViewPrice = (TextView) rootView.findViewById(R.id.textViewPrice);
        textViewServiceType = (TextView) rootView.findViewById(R.id.textViewServiceType);
        textViewCountry = (TextView) rootView.findViewById(R.id.textViewCountry);
        textViewCategory = (TextView) rootView.findViewById(R.id.textViewCategory);
        textViewDeadline = (TextView) rootView.findViewById(R.id.textViewDeadline);
        textViewStatus = (TextView) rootView.findViewById(R.id.textViewStatus);

        //Initializing the map view
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //Disable the bottom toolbar
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                // Warning is fine
                googleMap.setMyLocationEnabled(true);

                LatLng nus = new LatLng(1.2966, 103.7764);
                //googleMap.addMarker(new MarkerOptions().position(nus).title("NUS").snippet("Orbital 2017"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(nus).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        return rootView;
    }

    private View.OnClickListener ButtonPrevClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Get prev item index
            if(curItemIndex - 1 >= 0) {
                curItemIndex--;
            } else {
                curItemIndex = arrSize - 1;
            }
            //Set prev item's information
            CameraPosition cameraPosition = new CameraPosition.Builder().target(LocList.get(curItemIndex)).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Glide
                    .with(getContext())
                    .load(ImgList.get(curItemIndex))
                    .transform(new CircleTransform(getContext()))
                    //.bitmapTransform(new RoundedCornersTransformation(getContext(),20,20))
                    .into(imageViewProduct);
            textViewProductName.setText(ProductNameList.get(curItemIndex));
            textViewPrice.setText("$" + PriceList.get(curItemIndex));
            textViewServiceType.setText(ServiceTypeList.get(curItemIndex));
            textViewCountry.setText(CountryList.get(curItemIndex));
            textViewCategory.setText(CategoryList.get(curItemIndex));
            textViewDeadline.setText(DateList.get(curItemIndex));
            textViewStatus.setText(StatusList.get(curItemIndex));
        }
    };

    private View.OnClickListener ButtonNextClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Get next item index
            if(curItemIndex + 1 < arrSize) {
                curItemIndex++;
            } else {
                curItemIndex = 0;
            }
            //Set next item's information
            CameraPosition cameraPosition = new CameraPosition.Builder().target(LocList.get(curItemIndex)).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Glide
                    .with(getContext())
                    .load(ImgList.get(curItemIndex))
                    .transform(new CircleTransform(getContext()))
                    .into(imageViewProduct);
            textViewProductName.setText(ProductNameList.get(curItemIndex));
            textViewPrice.setText("$" + PriceList.get(curItemIndex));
            textViewServiceType.setText(ServiceTypeList.get(curItemIndex));
            textViewCountry.setText(CountryList.get(curItemIndex));
            textViewCategory.setText(CategoryList.get(curItemIndex));
            textViewDeadline.setText(DateList.get(curItemIndex));
            textViewStatus.setText(StatusList.get(curItemIndex));
        }
    };

    public void onStart() {
        super.onStart();
        //textViewMyRequests.setText(userEmail2); //For debugging if email was passed
        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    //Filter results to show only products by the user
                    String buyerEmail = product.getProductBuyer();
                    String courierEmail = product.getProductCourier();
                    String productStatus = product.getStatus();
                    if((buyerEmail.equals(userEmail) || courierEmail.equals(userEmail)) && !productStatus.equals("Completed")){
                        //Add individual product details into array list (room for improvement)
                        String[] latlong =  product.getProductCoords().split(",");
                        double latitude = Double.parseDouble(latlong[0]);
                        double longitude = Double.parseDouble(latlong[1]);
                        LatLng productLocation = new LatLng(latitude,longitude);
                        LocList.add(new LatLng(latitude,longitude));

                        ImgList.add(product.getImgurl());

                        ProductNameList.add(product.getProductName());

                        PriceList.add(product.getPrice());

                        if(buyerEmail.equals(userEmail)){
                            ServiceTypeList.add("Buyer");
                        } else {
                            ServiceTypeList.add("Courier");
                        }

                        CountryList.add(product.getCountry());

                        CategoryList.add(product.getProductType());

                        DateList.add(product.getDate());

                        StatusList.add(product.getStatus());

                        googleMap.addMarker(new MarkerOptions().position(productLocation).title(product.getProductName()).snippet(product.getDate()));
                    }
                }
                //Initialize first item to be display
                arrSize = LocList.size();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(LocList.get(curItemIndex)).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                Glide
                        .with(getContext())
                        .load(ImgList.get(curItemIndex))
                        .transform(new CircleTransform(getContext()))
                        .into(imageViewProduct);
                textViewProductName.setText(ProductNameList.get(curItemIndex));
                textViewPrice.setText("$" + PriceList.get(curItemIndex));
                textViewServiceType.setText(ServiceTypeList.get(curItemIndex));
                textViewCountry.setText(CountryList.get(curItemIndex));
                textViewCategory.setText(CategoryList.get(curItemIndex));
                textViewDeadline.setText(DateList.get(curItemIndex));
                textViewStatus.setText(StatusList.get(curItemIndex));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
