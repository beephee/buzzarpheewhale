package com.example.android.firebaseauthdemo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;

import okio.Utf8;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextBuyerCountry;
    private TextView textViewSignin;
    private TextView textViewGuestLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonSignup);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextBuyerCountry = (EditText) findViewById(R.id.editTextBuyerCountry);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        textViewGuestLogin = (TextView) findViewById(R.id.textViewGuestLogin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        textViewGuestLogin.setOnClickListener(this);
    }

    private void registerUser(){
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String buyerCountry = editTextBuyerCountry.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty.
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }

        if(TextUtils.isEmpty(password)){
            //password is empty.
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }

        //if valid field, show a progressbar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userUID = user.getUid();
                            String userType = "registered";
                            String blacklisted = "false";
                            String tutorial = "0";
                            String custsvc = "0";
                            Boolean courierActive = false;
                            String courierCountry = "NONE";
                            String maxWeight = "0";
                            String dateDeparture = "31/12/2017"; //default, can be changed
                            String buyerBudget = "NONE";
                            String bankAccount = "NONE";
                            User newUser = new User(userUID, email, userType, blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
                            dbRef.child(userUID).setValue(newUser);
                            //Successfully registered
                            //Start profile activity
                            //Display toast only
                            Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Could not register, please try again", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    @TargetApi(19)
    public void onClick(View view){
        if(view == buttonRegister){
            registerUser();
        }

        if(view == textViewSignin){
            //Open login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == textViewGuestLogin){
            progressDialog.setMessage("Logging in as guest...");
            progressDialog.show();
            String epw64 = "ZGFiYW80bWU=";
            byte[] epw = Base64.decode(epw64, Base64.DEFAULT);
            String epwStr = new String(epw, StandardCharsets.UTF_8);

            firebaseAuth.signInWithEmailAndPassword("guest@dabao4me.com", epwStr)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            //if the task is successful
                            if(task.isSuccessful()){
                                //start the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Guest login is disabled currently", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
