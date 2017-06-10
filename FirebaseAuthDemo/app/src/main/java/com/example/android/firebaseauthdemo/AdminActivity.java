package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.BOOLEAN;

public class AdminActivity extends AppCompatActivity {

    DatabaseReference databaseUsers;
    ListView listViewUsers;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        userList = new ArrayList<>();
        listViewUsers = (ListView) findViewById(R.id.listViewUsers);

        listViewUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = userList.get(i);
                showMenuDialog(user.getuserUID(),user.getuserEmail(),user.getuserType(),user.getBlacklisted());
                return true;
            }
        });

    }

    private void showMenuDialog(final String userUID, final String userEmail, final String userType, final String blacklisted) {

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

        banUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                banUser(userUID, userEmail, userType, blacklisted);
                b.dismiss();
            }
        });

        unbanUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbanUser(userUID, userEmail, userType, blacklisted);
                b.dismiss();
            }
        });

        setAdminUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAdminUser(userUID, userEmail, userType, blacklisted);
                b.dismiss();
            }
        });

        unadminUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unadminUser(userUID, userEmail, userType, blacklisted);
                b.dismiss();
            }
        });
    }

    private boolean banUser(String userUID, String userEmail, String userType, String blacklisted) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, userType, "true");
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User banned!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean unbanUser(String userUID, String userEmail, String userType, String blacklisted) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, userType, "false");
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User unbanned!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean setAdminUser(String userUID, String userEmail, String userType, String blacklisted) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, "admin", blacklisted);
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User given admin status!", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean unadminUser(String userUID, String userEmail, String userType, String blacklisted) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(userUID);
        User user = new User(userUID, userEmail, "registered", blacklisted);
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
                userList.clear();
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    userList.add(user);
                }
                UserList adapter = new UserList(AdminActivity.this, userList);
                listViewUsers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
