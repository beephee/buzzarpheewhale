package com.example.android.firebaseauthdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity{

    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_title;

    private String user_name,room_name, product_name, is_admin,is_custsvc;
    private DatabaseReference root ;
    private DatabaseReference userDB;
    private String temp_key;
    private static final int GALLERY_INTENT = 2;

    FirebaseAuth firebaseAuth;
    FirebaseUser curUser;
    ListView listViewChat;
    ChatList adapter;
    List<Chat> chatList;
    String imgurl;

    //Upload Image
    Button btn_send_img;
    ProgressDialog mProgressDialog;
    StorageReference mStorage;
    Uri downloadUrl;
    AlertDialog.Builder dialogBuilder;
    AlertDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        firebaseAuth = FirebaseAuth.getInstance();
        curUser = firebaseAuth.getCurrentUser();
        chatList = new ArrayList<>();
        listViewChat = (ListView) findViewById(R.id.listViewChat);
        listViewChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listViewChat.setStackFromBottom(true);

        btn_send_msg = (Button) findViewById(R.id.btn_send);
        btn_send_img = (Button) findViewById(R.id.btn_gallery);
        input_msg = (EditText) findViewById(R.id.msg_input);
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
        mProgressDialog = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();

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

        btn_send_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (curUser.getEmail().equals("guest@dabao4me.com")) {
                    /* Not viable as the method would be referencing an incorrect context
                    AddRequestActivity ara = new AddRequestActivity();
                    ara.showGuestDialog();
                    */
                    showGuestDialog();
                } else {
                    Date curDate = new Date();
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(curDate);
                    String formattedTime = new SimpleDateFormat("hh:mm:ss").format(curDate);

                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("name", user_name);
                    map2.put("msg", input_msg.getText().toString());
                    map2.put("date", formattedDate);
                    map2.put("time", formattedTime);
                    map2.put("uid", curUser.getUid());

                    message_root.updateChildren(map2);
                    input_msg.setText("");
                    if (is_custsvc.equals("true")) {
                        if (is_admin.equals("false")) {
                            userDB.child("custsvc").setValue("1");
                        } else {
                            userDB.child("custsvc").setValue("0");
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            toggleUploadDialog("Uploading image...");
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("Chat").child(room_name).child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();

                    Date curDate = new Date();
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(curDate);
                    String formattedTime = new SimpleDateFormat("hh:mm:ss").format(curDate);

                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);

                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("name", user_name);
                    map2.put("msg", downloadUrl.toString());
                    map2.put("date", formattedDate);
                    map2.put("time", formattedTime);
                    map2.put("uid", curUser.getUid());

                    message_root.updateChildren(map2);
                    input_msg.setText("");
                    if (is_custsvc.equals("true")) {
                        if (is_admin.equals("false")) {
                            userDB.child("custsvc").setValue("1");
                        } else {
                            userDB.child("custsvc").setValue("0");
                        }
                    }
                    loadingDialog.dismiss();
                    Toast.makeText(ChatRoomActivity.this, "Image sent!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void toggleUploadDialog(String message) {
        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.loading_dialog, null);
        dialogBuilder.setView(dialogView);

        final ImageView loadIcon = (ImageView) dialogView.findViewById(R.id.rotateIcon);
        final RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final TextView loadingMsg = (TextView) dialogView.findViewById(R.id.loadingMsg);
        loadingMsg.setText(message);

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
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    public void showGuestDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
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
                startActivity(new Intent(ChatRoomActivity.this, LoginActivity.class));
                b.dismiss();
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
