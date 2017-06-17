package com.example.android.firebaseauthdemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import static com.bumptech.glide.Glide.with;
import static com.example.android.firebaseauthdemo.R.id.imageViewProduct;
import static com.example.android.firebaseauthdemo.R.id.listViewUsers;


public class ChatList extends ArrayAdapter<Chat>{

    private Activity context;
    private List<Chat> chatList;
    String userID;
    String imgUrl;

    public ChatList(Activity context, List<Chat> chatList){
        super(context, R.layout.chat_list_layout, chatList);
        this.context = context;
        this.chatList= chatList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewChat = inflater.inflate(R.layout.chat_list_layout, null, true);

        Chat chat = chatList.get(position);
        final CircularImageView imageViewProfilePic = (CircularImageView) listViewChat.findViewById(R.id.profilePicture);
        final ImageView imageViewPlaceholder = (ImageView) listViewChat.findViewById(R.id.placeHolder);

        //User Info
        userID = chat.getUid();
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgUrl = dataSnapshot.child("profilepic").getValue(String.class);
                if(imgUrl != null){
                    imageViewPlaceholder.setVisibility(View.INVISIBLE);
                    Glide
                            .with(context)
                            .load(imgUrl)
                            .transform(new CircleTransform(context))
                            .into(imageViewProfilePic);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        TextView textViewUserName = (TextView) listViewChat.findViewById(R.id.textViewUserName);
        TextView textViewMessage = (TextView) listViewChat.findViewById(R.id.textViewMessage);
        TextView textViewDate = (TextView) listViewChat.findViewById(R.id.textViewDate);
        TextView textViewTime = (TextView) listViewChat.findViewById(R.id.textViewTime);




        textViewUserName.setText(chat.getName());
        textViewMessage.setText(chat.getMsg());
        textViewDate.setText(chat.getDate());
        textViewTime.setText(chat.getTime());

        return listViewChat;
    }


}
