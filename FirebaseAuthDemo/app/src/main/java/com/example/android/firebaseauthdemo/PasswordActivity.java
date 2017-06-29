package com.example.android.firebaseauthdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {


    //defining views
    private Button buttonSendEmail;
    private EditText editTextEmail;
    private static final String TAG = "PasswordActivity";

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonSendEmail = (Button) findViewById(R.id.buttonSendEmail);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSendEmail.setOnClickListener(this);
    }

    private void sendEmail() {
        String email = editTextEmail.getText().toString().trim();
        if (!email.contains("@")) {
            Toast.makeText(this,"Please enter valid email",Toast.LENGTH_LONG).show();
        }
        else {
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent");
                            }
                        }
                    });
            finish();
            Toast.makeText(this,"Please check your email for the password reset link",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSendEmail){
            sendEmail();
        }
    }
}
