package com.haihoangtran.pm.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.haihoangtran.pm.R;
import com.haihoangtran.pm.activities.HomeActivity;
import com.haihoangtran.pm.adapters.UsersAdapter;

import java.util.ArrayList;

import controller.database.UserDB;
import model.UserModel;

public class UserListDialog extends DialogFragment implements UserDialog.OnInputListener{
    private Button addUserBtn;
    private ListView usersLV;
    private UserDB userDB;

    public UserListDialog(UserDB userDB){
        this.userDB = userDB;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_list_dialog, container, false);

        // Initialize all element
        this.addUserBtn = view.findViewById(R.id.add_user_btn);
        this.usersLV = view.findViewById(R.id.users_list_view);

        // Handle Add User button
        this.addUserBtnHandle();

        // Handle User List View
        this.userListViewHandle();
        return view;
    }


    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/

    // Handle for Add New User
    @Override
    public void sendUser(int actionType, UserModel user){
        userDB.addUser(user.getFullName(), user.getBalance(), false);
        // Update list view to display new User.
        this.userListViewHandle();
    }

    // Handle Add User button
    private void addUserBtnHandle(){
        this.addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDialog userDialog = new UserDialog(1,
                                                        new UserModel(-1, "",
                                                                     0.00, false),
                                                        false);
                userDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
                // call user dialog as chile fragement
                userDialog.show(getChildFragmentManager(), "userDialogUserList");
            }
        });
    }

    // using override sendUser from this fragment.
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof UserDialog) {
            ((UserDialog) fragment).userInputListener = this;
        }
    }

    //*******************       List view       **************************
    private void userListViewHandle(){
        final ArrayList <UserModel> users = this.userDB.getAllUsers();
        UsersAdapter usersAdapter = new UsersAdapter(getActivity(), 0, users);
        this.usersLV.setAdapter(usersAdapter);

        // Handle click on item
        this.usersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserModel selectedUser = users.get(position);
                userDB.updateSelectedUser(selectedUser.getUserID());
                getDialog().dismiss();
                getActivity().recreate();
            }
        });
    }
}
