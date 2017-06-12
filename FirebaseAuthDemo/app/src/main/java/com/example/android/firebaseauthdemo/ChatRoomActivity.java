package com.example.android.firebaseauthdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.firebaseauthdemo.R;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by filipp on 6/28/2016.
 */
public class ChatRoomActivity extends AppCompatActivity{

    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;
    private TextView chat_title;

    private String user_name,room_name, product_name, is_admin;
    private DatabaseReference root ;
    private DatabaseReference userDB;
    private String temp_key;

    FirebaseAuth firebaseAuth;
    FirebaseUser curUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();

        btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView) findViewById(R.id.textView);
        chat_title = (TextView) findViewById(R.id.chatName);

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        product_name = getIntent().getExtras().get("product_name").toString();
        is_admin = getIntent().getExtras().get("is_admin").toString();
        setTitle("[Product] "+ product_name);
        chat_title.setText(product_name);

        root = FirebaseDatabase.getInstance().getReference().child("chat").child(room_name);
        userDB = FirebaseDatabase.getInstance().getReference().child("users").child(curUser.getUid());

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg",input_msg.getText().toString());

                message_root.updateChildren(map2);
                input_msg.setText("");
                if(is_admin.equals("false")){
                    userDB.child("custsvc").setValue("1");
                } else {
                    userDB.child("custsvc").setValue("0");
                }
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String chat_msg,chat_user_name;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            String chat_user_name_temp = (String) ((DataSnapshot)i.next()).getValue();
            if(chat_user_name_temp.equals(user_name)){
                chat_user_name = "You";
            } else {
                chat_user_name = chat_user_name_temp;
            }
            chat_conversation.append("[" + chat_user_name + "] " + chat_msg +" \n\n");
        }


    }
}
