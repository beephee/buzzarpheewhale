package com.example.android.firebaseauthdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button searchBtn;
    TextView weatherTV;
    EditText addressET;
    String country = "Singapore"; //Default otherwise shown as null before performing a search

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Geocoder
        weatherTV = (TextView) findViewById(R.id.weatherTV);
        addressET = (EditText) findViewById(R.id.addressET);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBtn.getApplicationWindowToken(), 0);
                if (TextUtils.isEmpty(addressET.getText().toString())) {
                    Toast.makeText(MapsActivity.this, "Please enter address.", Toast.LENGTH_LONG).show();
                } else {
                    String address = addressET.getText().toString();
                    updateMap(address);
                }
            }
        });

    }

    public void updateMap(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressResult = geocoder.getFromLocationName(address, 1);
            if (!addressResult.isEmpty()) {
                Address selectedResult = addressResult.get(0);
                Double newLat = selectedResult.getLatitude();
                Double newLong = selectedResult.getLongitude();
                String countryName = selectedResult.getCountryName();
                String countryCode = selectedResult.getCountryCode();
                LatLng userLocation = new LatLng(newLat, newLong);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Chosen location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                APITask task = new APITask();
                task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ countryName + "," + countryCode +"&appid=3da1afc0881c8f5d20201530a7bdc7d5");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class APITask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("POST");
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override // method is called when doinbackground method is complete, parse the return from there to here
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject object = new JSONObject(result);
                String weather = object.getString("weather");
                country = object.getString("name");
                JSONArray weatherInfo = new JSONArray(weather);
                JSONObject mainInfo = weatherInfo.getJSONObject(0);
                String weatherDetail = country + "'s Weather Today\n " + mainInfo.getString("main") + "\n" + mainInfo.getString("description");
                weatherTV.setText(weatherDetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("picked_point",latLng);
                returnIntent.putExtra("picked_country",country);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        // Add a marker in NUS and move the camera
        LatLng nus = new LatLng(1.2966, 103.7764);
        mMap.addMarker(new MarkerOptions().position(nus).title("Marker in NUS"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nus, 14.0f));
    }


}
