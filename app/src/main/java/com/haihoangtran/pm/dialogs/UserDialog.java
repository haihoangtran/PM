package com.haihoangtran.pm.dialogs;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.haihoangtran.pm.R;

import model.UserModel;

public class UserDialog extends DialogFragment {
    private UserModel user;
    private int actionType;
    private boolean is_activity;
    private TextView title, errorTxt;
    private EditText name, balance;
    private Button cancelBtn, confirmBtn;

    public interface OnInputListener{
        void sendUser(int actionType, UserModel user);
    }

    public OnInputListener userInputListener;

    public UserDialog(int actionType, UserModel user, boolean is_activity){
        this.actionType = actionType;
        this.user = user;
        this.is_activity = is_activity;             // for onAttach know where data is sent back to
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_add_edit_dialog, container, false);
        if (this.actionType == 1) {
            getDialog().setCanceledOnTouchOutside(false);
        }
        title = view.findViewById(R.id.dialog_title);
        name = view.findViewById(R.id.user_name_edit_text);
        balance = view.findViewById(R.id.user_balance_edit_text);
        errorTxt = view.findViewById(R.id.error_text);
        cancelBtn = view.findViewById(R.id.add_edit_dialog_cancel);
        confirmBtn = view.findViewById(R.id.add_edit_dialog_confirm);

        //Initialize some elements
        title.setText((this.actionType == 1 ? getString(R.string.add) : getString(R.string.edit)));
        name.setText(user.getFullName());
        balance.setText((user.getBalance() == 0.0 ? "0.00" : String.format("%.2f", user.getBalance())));
        errorTxt.setVisibility(View.INVISIBLE);

        // Handle Cancel button
        this.cancelBtnHandle();

        //Handle Confirm button
        this.confirmBtnHandle();

        return view;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/

    //Handle Cancel button
    private void cancelBtnHandle(){
        cancelBtn.setClickable((this.actionType != 1));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    // Handle confirm button
    private void confirmBtnHandle(){
        confirmBtn.setText((this.actionType == 1 ? getString(R.string.add) : getString(R.string.edit)));

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = name.getText().toString();
                String user_balance = balance.getText().toString();
                if (user_name.matches("")){
                    errorTxt.setVisibility(View.VISIBLE);
                }else {
                    UserModel new_user = new UserModel(-1, user_name, Double.parseDouble(user_balance), false);
                    userInputListener.sendUser(actionType, new_user);
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            userInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}

