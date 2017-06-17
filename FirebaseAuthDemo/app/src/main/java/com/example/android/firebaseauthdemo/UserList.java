package com.example.android.firebaseauthdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class UserList extends ArrayAdapter<User>{

    private Activity context;
    private List<User> userList;

    public UserList(Activity context, List<User> userList){
        super(context, R.layout.user_list_layout, userList);
        this.context = context;
        this.userList= userList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewUsers = inflater.inflate(R.layout.user_list_layout, null, true);

        RelativeLayout layoutWrap = (RelativeLayout) listViewUsers.findViewById(R.id.layoutWrap);
        TextView textViewUserEmail = (TextView) listViewUsers.findViewById(R.id.textViewUserEmail);
        TextView textViewUserType = (TextView) listViewUsers.findViewById(R.id.textViewUserType);
        TextView textViewUID = (TextView) listViewUsers.findViewById(R.id.textViewUID);
        TextView textViewBlacklist = (TextView) listViewUsers.findViewById(R.id.textViewBlacklist);
        ImageView adminIcon = (ImageView) listViewUsers.findViewById(R.id.adminIcon);
        ImageView bannedIcon = (ImageView) listViewUsers.findViewById(R.id.bannedIcon);
        ImageView custsvcIcon = (ImageView) listViewUsers.findViewById(R.id.custsvcIcon);

        User user = userList.get(position);

        if(user != null) {
            if (user.getBlacklisted().equals("true")) {
                bannedIcon.setVisibility(View.VISIBLE);
            }
            if (user.getuserType().equals("admin")) {
                adminIcon.setVisibility(View.VISIBLE);
            }
            if (user.getCustsvc().equals("1")) {
                custsvcIcon.setVisibility(View.VISIBLE);
            }
        }
        textViewUserEmail.setText(user.getuserEmail());
        textViewUserType.setText(user.getuserType());
        textViewUID.setText(user.getuserUID());
        textViewBlacklist.setText(user.getBlacklisted());

        return listViewUsers;
    }


}
