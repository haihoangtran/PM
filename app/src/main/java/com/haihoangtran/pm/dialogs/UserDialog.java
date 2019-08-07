package com.haihoangtran.pm.dialogs;

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

import com.haihoangtran.pm.R;

import model.UserModel;

public class UserDialog extends DialogFragment {
    private UserModel user;
    private int actionType;
    private TextView title, errorTxt;
    private EditText name, balance;
    private Button cancelBtn, confirmBtn;

    public interface OnInputListener{
        void sendUser(int actionType, UserModel user);
    }

    public OnInputListener userInputListener;

    public UserDialog(int actionType, UserModel user){
        this.actionType = actionType;
        this.user = user;
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
        balance.setText((user.getBalance() == 0.0 ? "" : String.format("%.2f", user.getBalance())));
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
        cancelBtn.setVisibility((this.actionType == 1 ? View.INVISIBLE : View.VISIBLE));
        cancelBtn.setClickable((this.actionType == 1 ? false : true));
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
                if (user_name.matches("") || user_balance.matches("")){
                    errorTxt.setVisibility(View.VISIBLE);
                }else {
                    UserModel new_user = new UserModel(user_name, Double.parseDouble(user_balance));
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

