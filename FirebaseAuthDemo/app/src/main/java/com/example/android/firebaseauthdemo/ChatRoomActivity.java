package com.example.android.firebaseauthdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.android.firebaseauthdemo.R.id.listViewChat;
import static junit.runner.Version.id;

/**
 * Created by filipp on 6/28/2016.
 */
public class ChatRoomActivity extends AppCompatActivity{

    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;
    private TextView chat_title;

    private String user_name,room_name, product_name, is_admin,is_custsvc;
    private DatabaseReference root ;
    private DatabaseReference userDB;
    private String temp_key;

    FirebaseAuth firebaseAuth;
    FirebaseUser curUser;
    ListView listViewChat;
    ChatList adapter;
    List<Chat> chatList;
    String imgurl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();
        chatList = new ArrayList<>();
        listViewChat = (ListView) findViewById(R.id.listViewChat);

        btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView) findViewById(R.id.textView);
        chat_title = (TextView) findViewById(R.id.chatName);

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        product_name = getIntent().getExtras().get("product_name").toString();
        is_admin = getIntent().getExtras().get("is_admin").toString();
        is_custsvc = getIntent().getExtras().get("is_custsvc").toString();
        setTitle("[Product] "+ product_name);
        chat_title.setText(product_name);

        root = FirebaseDatabase.getInstance().getReference().child("chat").child(room_name);
        userDB = FirebaseDatabase.getInstance().getReference().child("users").child(room_name);

        //User Info
        firebaseAuth = FirebaseAuth.getInstance();
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgurl = dataSnapshot.child("profilepic").getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date curDate = new Date();
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(curDate);
                String formattedTime = new SimpleDateFormat("hh:mm:ss").format(curDate);

                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);
                map2.put("msg",input_msg.getText().toString());
                map2.put("date",formattedDate);
                map2.put("time",formattedTime);
                map2.put("uid",curUser.getUid());

                message_root.updateChildren(map2);
                input_msg.setText("");
                if(is_admin.equals("false") && is_custsvc.equals("true")){
                    userDB.child("custsvc").setValue("1");
                } else {
                    userDB.child("custsvc").setValue("0");
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot chatSnapshot : dataSnapshot.getChildren()){
                    Chat chat = chatSnapshot.getValue(Chat.class);
                    chatList.add(chat);
                }
                adapter = new ChatList(ChatRoomActivity.this, chatList);
                listViewChat.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
