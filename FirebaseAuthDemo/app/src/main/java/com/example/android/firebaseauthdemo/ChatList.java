package com.example.android.firebaseauthdemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;

import java.util.List;

import static com.example.android.firebaseauthdemo.R.id.listViewUsers;


public class ChatList extends ArrayAdapter<Chat>{

    private Activity context;
    private List<Chat> chatList;

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

        TextView textViewUserName = (TextView) listViewChat.findViewById(R.id.textViewUserName);
        TextView textViewMessage = (TextView) listViewChat.findViewById(R.id.textViewMessage);
        TextView textViewDate = (TextView) listViewChat.findViewById(R.id.textViewDate);
        TextView textViewTime = (TextView) listViewChat.findViewById(R.id.textViewTime);

        Chat chat = chatList.get(position);

        textViewUserName.setText(chat.getName());
        textViewMessage.setText(chat.getMsg());
        textViewDate.setText(chat.getDate());
        textViewTime.setText(chat.getTime());

        return listViewChat;
    }


}
