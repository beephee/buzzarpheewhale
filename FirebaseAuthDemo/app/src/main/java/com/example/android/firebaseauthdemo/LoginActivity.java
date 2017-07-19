package com.example.android.firebaseauthdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView textViewPassword;

    //Firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbRef;

    //Loading Dialog
    AlertDialog.Builder dialogBuilder;
    AlertDialog loadingDialog;

    //Facebook Login
    CallbackManager mCallbackManager;
    private static final String TAG = "FacebookLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //If the objects getcurrentuser method is not null
        //means user is already logged in
        if(firebaseAuth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        //Initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup  = (TextView) findViewById(R.id.textViewSignUp);
        textViewPassword  = (TextView) findViewById(R.id.textViewForgotPassword);

        //Attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textViewPassword.setOnClickListener(this);

        //Facebook Auth
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                editTextEmail.setVisibility(View.INVISIBLE);
                editTextPassword.setVisibility(View.INVISIBLE);
                buttonSignIn.setText("LOGGING IN WITH FACEBOOK...");
                buttonSignIn.setEnabled(false);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            //Grab initial Firebase Data
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            final String userUID = user.getUid();
                            dbRef = FirebaseDatabase.getInstance().getReference().child("users");
                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userUID)){
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    } else {
                                        fbRegister();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void fbRegister(){
        toggleLoadingDialog("Creating account with Facebook credentials...");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userEmail = user.getEmail();
        String UID = user.getUid();
        String buyerCountry = "NONE";
        String userType = "registered";
        String blacklisted = "false";
        String tutorial = "0";
        String custsvc = "0";
        Boolean courierActive = false;
        String courierCountry = "NONE";
        String maxWeight = "0";
        String dateDeparture = "31/12/2017";
        String buyerBudget = "NONE";
        String bankAccount = "NONE";
        User newUser = new User(UID, userEmail, userType, blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
        dbRef.child(UID).setValue(newUser);
        loadingDialog.dismiss();
    }

    private boolean isEmailValid (String email) {
        return email.contains("@");
    }

    //Method for user login
    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //Checking if email and passwords are empty
        if(TextUtils.isEmpty(email) || !isEmailValid(email)){
            Toast.makeText(this,"Please enter a valid email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter a valid password",Toast.LENGTH_LONG).show();
            return;
        }

        //If the email and password are not empty
        //displaying a progress dialog

        //progressDialog.setMessage("Logging in, please wait...");
        //progressDialog.show();
        toggleLoadingDialog("Logging in...");

        //Logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.dismiss();
                        loadingDialog.dismiss();
                        //if the task is successful
                        if(task.isSuccessful()){
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void toggleLoadingDialog(String message) {
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.login_dialog, null);
        dialogBuilder.setView(dialogView);

        final ImageView loadIcon = (ImageView) dialogView.findViewById(R.id.rotateIcon);
        final RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final TextView loginMsg = (TextView) dialogView.findViewById(R.id.loginMsg);

        loginMsg.setText(message);
        rotate.setDuration(1000);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        loadIcon.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadIcon.startAnimation(rotate);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        loadingDialog = dialogBuilder.create();
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.show();
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }

        if(view == textViewSignup){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if(view == textViewPassword){
            finish();
            startActivity(new Intent(this, PasswordActivity.class));
        }
    }
}
