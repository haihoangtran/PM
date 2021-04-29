package com.haihoangtran.pm.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.haihoangtran.pm.R;
import com.haihoangtran.pm.dialogs.UserListDialog;

import java.util.ArrayList;
import java.util.List;
import model.UserModel;

public class UsersAdapter extends ArrayAdapter<UserModel> {
    private Context context;
    private List<UserModel> userRecords;

    public UsersAdapter(Context context, int resource, ArrayList<UserModel> userRecords){
        super(context, resource, userRecords);
        this.context = context;
        this.userRecords = userRecords;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        // get Specific User by position
        UserModel user = userRecords.get(position);
        View view = convertView;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.user_record_cell, null);
        }
        TextView userName = view.findViewById(R.id.user_fullname_txt);
        userName.setText(user.getFullName());
        return view;
    }
}
