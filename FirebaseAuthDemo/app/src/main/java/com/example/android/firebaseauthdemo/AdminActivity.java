package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.firebaseauthdemo.R.id.buttonGetCoordinates;
import static com.example.android.firebaseauthdemo.R.id.listViewProducts;
import static java.sql.Types.BOOLEAN;

public class AdminActivity extends AppCompatActivity {

    DatabaseReference databaseUsers;
    ListView listViewUsers;
    List<User> userList;
    FirebaseAuth firebaseAuth;
    String listFilter;
    UserList adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listFilter = "all";
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        userList = new ArrayList<>();
        listViewUsers = (ListView) findViewById(R.id.listViewUsers);

        listViewUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = userList.get(i);
                showMenuDialog(user.getuserUID(),user.getuserEmail(),user.getuserType(),user.getBlacklisted(),user.getTutorial(),user.getCustsvc(), user.getCourierActive(), user.getBuyerCountry(), user.getCourierCountry(), user.getMaxWeight(), user.getDateDeparture(), user.getBuyerBudget(), user.getBankAccount());
                return true;
            }
        });

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = userList.get(i);
                String productID = user.getuserUID();
                String productName = user.getuserEmail().split("@", 2)[0] + " - CUSTOMER SERVICE";
                Intent intent = new Intent(AdminActivity.this, ChatRoomActivity.class);
                intent.putExtra("room_name", productID);
                intent.putExtra("product_name", productName);
                intent.putExtra("user_name", "Staff");
                intent.putExtra("is_admin", "true");
                intent.putExtra("is_custsvc", "true");
                startActivity(intent);
            }
        });

        //Primary verification of user's current status
        firebaseAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.child("userType").getValue(String.class);
                if(!userType.equals("admin")){
                    showUnauthorisedDialog();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Filter Buttons
        final Button btnAll = (Button) findViewById(R.id.buttonAll);
        btnAll.setTypeface(Typeface.DEFAULT_BOLD);
        btnAll.setTextSize(16);
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFilter = "all";
                onStart();
                adapter.notifyDataSetChanged();
                clearButtonStyle();
                btnAll.setTypeface(Typeface.DEFAULT_BOLD);
                btnAll.setTextSize(16);
            }
        });
        final Button btnCustsvc = (Button) findViewById(R.id.buttonCustsvc);
        btnCustsvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFilter = "custsvc";
                onStart();
                adapter.notifyDataSetChanged();
                clearButtonStyle();
                btnCustsvc.setTypeface(Typeface.DEFAULT_BOLD);
                btnCustsvc.setTextSize(16);
            }
        });
        final Button btnAdmin = (Button) findViewById(R.id.buttonAdmin);
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFilter = "admin";
                onStart();
                adapter.notifyDataSetChanged();
                clearButtonStyle();
                btnAdmin.setTypeface(Typeface.DEFAULT_BOLD);
                btnAdmin.setTextSize(16);
            }
        });
        final Button btnBanned = (Button) findViewById(R.id.buttonBanned);
        btnBanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFilter = "banned";
                onStart();
                adapter.notifyDataSetChanged();
                clearButtonStyle();
                btnBanned.setTypeface(Typeface.DEFAULT_BOLD);
                btnBanned.setTextSize(16);
            }
        });
    }

    private void clearButtonStyle(){
        Button btnAll = (Button) findViewById(R.id.buttonAll);
        btnAll.setTypeface(Typeface.SANS_SERIF);
        btnAll.setTextSize(14);
        Button btnCustsvc = (Button) findViewById(R.id.buttonCustsvc);
        btnCustsvc.setTypeface(Typeface.SANS_SERIF);
        btnCustsvc.setTextSize(14);
        Button btnAdmin = (Button) findViewById(R.id.buttonAdmin);
        btnAdmin.setTypeface(Typeface.SANS_SERIF);
        btnAdmin.setTextSize(14);
        Button btnBanned = (Button) findViewById(R.id.buttonBanned);
        btnBanned.setTypeface(Typeface.SANS_SERIF);
        btnBanned.setTextSize(14);
    }

    private void showMenuDialog(final String userUID, final String userEmail, final String userType, final String blacklisted, final String tutorial, final String custsvc, final Boolean courierActive, final String buyerCountry, final String courierCountry, final String maxWeight, final String dateDeparture, final String buyerBudget, final String bankAccount) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.admin_menu, null);
        dialogBuilder.setView(dialogView);
        final Button banUserButton = (Button) dialogView.findViewById(R.id.banButton);
        final Button unbanUserButton = (Button) dialogView.findViewById(R.id.unbanButton);
        final Button setAdminUserButton = (Button) dialogView.findViewById(R.id.setAdminButton);
        final Button unadminUserButton = (Button) dialogView.findViewById(R.id.unadminButton);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        b.show();

        //Secondary verification of user's current status
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.child("userType").getValue(String.class);
                if(!userType.equals("admin")){
                    showUnauthorisedDialog();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        banUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                banUser(userUID, userEmail, userType, blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
                b.dismiss();
            }
        });

        unbanUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbanUser(userUID, userEmail, userType, blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
                b.dismiss();
            }
        });

        setAdminUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAdminUser(userUID, userEmail, userType, blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
                b.dismiss();
            }
        });

        unadminUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unadminUser(userUID, userEmail, userType, blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
                b.dismiss();
            }
        });
    }

    private boolean banUser(String userUID, String userEmail, String userType, String blacklisted, String tutorial, String custsvc, Boolean courierActive, String buyerCountry, String courierCountry, String maxWeight, String dateDeparture, String buyerBudget, String bankAccount) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, userType, "true", tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User banned!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean unbanUser(String userUID, String userEmail, String userType, String blacklisted, String tutorial, String custsvc, Boolean courierActive, String buyerCountry, String courierCountry, String maxWeight, String dateDeparture, String buyerBudget, String bankAccount) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, userType, "false", tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User unbanned!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean setAdminUser(String userUID, String userEmail, String userType, String blacklisted, String tutorial, String custsvc, Boolean courierActive, String buyerCountry, String courierCountry, String maxWeight, String dateDeparture, String buyerBudget, String bankAccount) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, "admin", blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User given admin status!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean unadminUser(String userUID, String userEmail, String userType, String blacklisted, String tutorial, String custsvc, Boolean courierActive, String buyerCountry, String courierCountry, String maxWeight, String dateDeparture, String buyerBudget, String bankAccount) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, "registered", blacklisted, tutorial, custsvc, courierActive, buyerCountry, courierCountry, maxWeight, dateDeparture, buyerBudget, bankAccount);
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "Admin status removed!", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switch (listFilter) {
                    case "all":
                        userList.clear();
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            userList.add(user);
                        }
                        break;
                    case "custsvc":
                        userList.clear();
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            if(user.getCustsvc().equals("1")){
                                userList.add(user);
                            }
                        }
                        break;
                    case "admin":
                        userList.clear();
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            if(user.getuserType().equals("admin")){
                                userList.add(user);
                            }
                        }
                        break;
                    case "banned":
                        userList.clear();
                        for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                            User user = userSnapshot.getValue(User.class);
                            if(user.getBlacklisted().equals("true")){
                                userList.add(user);
                            }
                        }
                        break;
                }
                adapter = new UserList(AdminActivity.this, userList);
                listViewUsers.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void showUnauthorisedDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.unauth_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonOk = (Button) dialogView.findViewById(R.id.buttonOk);

        final AlertDialog b = dialogBuilder.create();
        b.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Ban user for unauthorized action
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        dR.child("blacklisted").setValue("true");

        b.show();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
                firebaseAuth.signOut();
                finish();
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
