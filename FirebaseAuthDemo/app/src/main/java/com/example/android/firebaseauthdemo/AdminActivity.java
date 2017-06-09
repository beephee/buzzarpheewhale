package com.example.android.firebaseauthdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
                //To add dialog with Ban/Unban/Set Admin
                return true;
            }
        });

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
