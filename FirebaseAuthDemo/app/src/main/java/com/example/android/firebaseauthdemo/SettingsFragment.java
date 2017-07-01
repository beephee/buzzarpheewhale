package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.R.attr.country;
import static android.app.Activity.RESULT_OK;
import static com.example.android.firebaseauthdemo.AddRequestActivity.PICK_MAP_POINT_REQUEST;
import static com.example.android.firebaseauthdemo.R.drawable.profilepic;
import static com.example.android.firebaseauthdemo.R.id.button;
import static com.example.android.firebaseauthdemo.R.id.buttonAdmin;
import static com.example.android.firebaseauthdemo.R.id.buttonGetCoordinates;
import static com.example.android.firebaseauthdemo.R.id.buttonGetImage;
import static com.example.android.firebaseauthdemo.R.id.listViewProducts;
import static com.example.android.firebaseauthdemo.R.id.textViewImageName;
import static java.lang.System.load;


public class SettingsFragment extends Fragment {

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    DatabaseReference databaseProducts;
    ListView listViewProducts;
    List<Product> productList;
    String userEmail;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUsers;
    View buttonView;
    String userBuyerCountry;
    String userMaxBudget;
    Boolean userCourierActive;
    String userCourierCountry;
    String userMaxWeight;
    String userDateDeparture;
    String userBankAccount;
    EditText editBuyerCountry;
    EditText editMaxBudget;
    Switch switchCourierActive;
    EditText editCourierCountry;
    EditText editMaxWeight;
    TextView editMaxDate;
    EditText editBankAccount;
    private static final String TAG = "SettingsFragment";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String newDate;

    //Profile Pic Variable
    CircularImageView profilePic;
    StorageReference mStorage;
    ProgressDialog mProgressDialog;
    Uri downloadUrl;
    private static final int GALLERY_INTENT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_settings, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
        }

        TextView username = (TextView) rootView.findViewById(R.id.accountText);
        username.setText(userEmail);

        Button btnAdmin = (Button) rootView.findViewById(R.id.buttonAdmin);
        btnAdmin.setOnClickListener(AdminButtonClickListener);

        Button btnUpdate = (Button) rootView.findViewById(R.id.updateSettingsButton);
        btnUpdate.setOnClickListener(updateClickListener);

        Button btnTransactHistory = (Button) rootView.findViewById(R.id.transactHistoryButton);
        btnTransactHistory.setOnClickListener(transactHistoryClickListener);

        Button btnLogOut = (Button) rootView.findViewById(R.id.logOut);
        btnLogOut.setOnClickListener(logOutButtonClickListener);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Set Profile Picture if exists
                String picurl = dataSnapshot.child("profilepic").getValue(String.class);
                if(picurl != null){
                    Glide
                            .with(getContext())
                            .load(picurl.toString())
                            .transform(new CircleTransform(getContext()))
                            .into(profilePic);
                }
                String userType = dataSnapshot.child("userType").getValue(String.class);
                if(userType.equals("admin")){
                    buttonView.setVisibility(View.VISIBLE);
                }

                userBuyerCountry = dataSnapshot.child("buyerCountry").getValue(String.class);
                userMaxBudget = dataSnapshot.child("buyerBudget").getValue(String.class);
                userCourierActive = dataSnapshot.child("courierActive").getValue(Boolean.class);
                userCourierCountry = dataSnapshot.child("courierCountry").getValue(String.class);
                userMaxWeight = dataSnapshot.child("maxWeight").getValue(String.class);
                userDateDeparture = dataSnapshot.child("dateDeparture").getValue(String.class);
                userBankAccount = dataSnapshot.child("bankAccount").getValue(String.class);
                editBuyerCountry = (EditText) rootView.findViewById(R.id.editTextBuyerCountry);
                editMaxBudget = (EditText) rootView.findViewById(R.id.editTextMaxBudget);
                switchCourierActive = (Switch) rootView.findViewById(R.id.activeSwitch);
                editCourierCountry = (EditText) rootView.findViewById(R.id.editTextCourierCountry);
                editMaxWeight = (EditText) rootView.findViewById(R.id.editTextMaxWeight);
                editMaxDate = (TextView) rootView.findViewById(R.id.editTextMaxDate);
                editBankAccount = (EditText) rootView.findViewById(R.id.editTextBankAccount);
                editBuyerCountry.setText(userBuyerCountry);
                editMaxBudget.setText(userMaxBudget);
                if (userCourierActive == false) {
                    switchCourierActive.setChecked(false);
                } else {
                    switchCourierActive.setChecked(true);
                }
                editCourierCountry.setText(userCourierCountry);
                editMaxWeight.setText(userMaxWeight);
                editMaxDate.setText(userDateDeparture);
                editBankAccount.setText(userBankAccount);

                editMaxDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeDate();
                        editMaxDate.setText(newDate);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Profile Picture
        mStorage = FirebaseStorage.getInstance().getReference();
        profilePic = (CircularImageView) rootView.findViewById(R.id.profilePicture);
        profilePic.setOnClickListener(updateProfilePicListener);
        mProgressDialog = new ProgressDialog(getActivity());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        buttonView = (View) getView().findViewById(buttonAdmin);
    }

    private View.OnClickListener updateProfilePicListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(userEmail.equals("guest@dabao4me.com")){
                showGuestDialogFragment();
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("Profile").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Image uploaded!", Toast.LENGTH_LONG).show();
                    Glide
                            .with(getContext())
                            .load(downloadUrl.toString())
                            .transform(new CircleTransform(getContext()))
                            .into(profilePic);
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("profilepic");
                    dR.setValue(downloadUrl.toString());

                }
            });
        }
    }

    private View.OnClickListener AdminButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AdminActivity.class);
            getActivity().startActivity(intent);
        }
    };

    private View.OnClickListener updateClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (userEmail.equals("guest@dabao4me.com")) {
                showGuestDialogFragment();
            } else {
                //update settings if all filled
                String newBuyerCountry = editBuyerCountry.getText().toString();
                String newMaxBudget = editMaxBudget.getText().toString();
                String newCourierCountry = editCourierCountry.getText().toString();
                String newMaxWeight = editMaxWeight.getText().toString();
                String newMaxDate = editMaxDate.getText().toString();
                String newBankAccount = editBankAccount.getText().toString();
                if (newBuyerCountry.equals("") || newMaxBudget.equals("") || newCourierCountry.equals("") || newMaxWeight.equals("") || newMaxDate.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill up all fields to update settings!", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("buyerCountry");
                    dR.setValue(newBuyerCountry);
                    DatabaseReference dR2 = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("buyerBudget");
                    dR2.setValue(newMaxBudget);
                    DatabaseReference dR3 = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("courierCountry");
                    dR3.setValue(newCourierCountry);
                    DatabaseReference dR4 = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("maxWeight");
                    dR4.setValue(newMaxWeight);
                    DatabaseReference dR5 = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("dateDeparture");
                    dR5.setValue(newMaxDate);
                    DatabaseReference dR6 = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("courierActive");
                    if (switchCourierActive.isChecked() == true) {
                        dR6.setValue(true);
                    } else {
                        dR6.setValue(false);
                    }
                    DatabaseReference dR7 = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getCurrentUser().getUid()).child("bankAccount");
                    dR7.setValue(newBankAccount);
                    Toast.makeText(getActivity().getApplicationContext(), "Settings updated!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    public void showGuestDialogFragment() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.guest_menu, null);
        dialogBuilder.setView(dialogView);

        final Button btnBack = (Button) dialogView.findViewById(R.id.btnBack);
        final Button btnLogin = (Button) dialogView.findViewById(R.id.btnLogin);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                b.dismiss();
            }
        });

    }

    private View.OnClickListener transactHistoryClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Fragment newFrag = new TransactionHistoryFragment();
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.enter_from_left,R.anim.exit_to_right);
            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            trans.replace(R.id.frame_layout, newFrag);
            trans.addToBackStack(null);
            trans.commit();
        }
    };

    private View.OnClickListener logOutButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    };

    // Update date
    private void changeDate() {
        final TextView showDatePicker = (TextView) getView().findViewById(R.id.editTextMaxDate);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                newDate = day + "/" + month + "/" + year;
            }
        };
    };
}
