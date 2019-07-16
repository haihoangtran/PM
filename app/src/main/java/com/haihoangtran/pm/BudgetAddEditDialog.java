package com.haihoangtran.pm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import model.RecordModel;
import model.UserModel;

public class BudgetAddEditDialog extends DialogFragment {

    private TextView title, errorTxt;
    private EditText date, place, amount;
    private CheckBox withdrawCb, depositCb;
    private Button cancelBtn, confirmBtn;
    private RecordModel initRecord;
    private int actionType;

    public interface OnInputListener{
        void sendRecord(int actionType, RecordModel record, RecordModel oldRecord);
    }

    public BudgetAddEditDialog.OnInputListener recordInputListener;


    public BudgetAddEditDialog(int actionType, RecordModel record) {
        this.actionType = actionType;
        this.initRecord = record;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buget_add_edit_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        title = view.findViewById(R.id.dialog_title);
        date = view.findViewById(R.id.date_edit_text);
        place = view.findViewById(R.id.place_edit_text);
        amount = view.findViewById(R.id.amount_edit_text);
        withdrawCb = view.findViewById(R.id.withdraw_cb);
        depositCb = view.findViewById(R.id.deposit_cb);
        cancelBtn = view.findViewById(R.id.add_edit_dialog_cancel);
        confirmBtn = view.findViewById(R.id.add_edit_dialog_confirm);
        errorTxt = view.findViewById(R.id.dialog_error_text);

        // Initialize text for some elements
        title.setText((this.actionType == 1 ? getString(R.string.add) : getString(R.string.edit)));
        errorTxt.setVisibility(View.INVISIBLE);
        date.setText(initRecord.getDate());
        place.setText(initRecord.getPlace());
        amount.setText((initRecord.getAmount() == 0.0 ? "" : String.format("%.2f", initRecord.getAmount())));
        depositCb.setChecked((initRecord.getTypeID() == 1 ? true: false));
        withdrawCb.setChecked((initRecord.getTypeID() == 2 ? true : false));

        // Handle Cancel button
        this.cancelBtnHandle();

        // Handle Confirm Button
        this.confirmBtnHandle();

        // Handle Checkbox buttons: 1 - deposit, 2 - withdraw
        this.checkBoxHandle(1);
        this.checkBoxHandle(2);

        return view;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/

    // Handle Cancel button
    private void cancelBtnHandle(){
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getDialog().dismiss();
            }
        });
    }


    // handle Checkbox btns
    private void checkBoxHandle(int type){
        switch(type) {
            case 1:
                depositCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        depositCb.setChecked(true);
                        withdrawCb.setChecked(false);
                    }
                });
                break;
            case 2:
                withdrawCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        depositCb.setChecked(false);
                        withdrawCb.setChecked(true);
                    }
                });
                break;

        }
    }

    // Handle confirm button
    private void confirmBtnHandle(){
        confirmBtn.setText((this.actionType == 1 ? getString(R.string.add).toUpperCase() : getString(R.string.edit).toUpperCase()));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String record_date = date.getText().toString();
                String record_place = place.getText().toString();
                String record_amount = amount.getText().toString();
                int record_typeID = 0;
                String record_typeName = "";
                if (depositCb.isChecked()){
                    record_typeID = 1;
                    record_typeName = getString(R.string.deposit);
                } else if (withdrawCb.isChecked()){
                    record_typeID = 2;
                    record_typeName = getString(R.string.withdraw);
                }
                if (record_date.matches("") || record_place.matches("") || record_amount.matches("")){
                    errorTxt.setVisibility(View.VISIBLE);
                }else {
                    RecordModel new_record = new RecordModel(initRecord.getRecordID(), record_date.split("/")[2], record_date, record_place,
                                                             Double.parseDouble(record_amount), record_typeID, record_typeName);
                    if (actionType == 1) {
                        recordInputListener.sendRecord(actionType, new_record, null);
                    }else{
                        recordInputListener.sendRecord(actionType, new_record, initRecord);
                    }
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            recordInputListener = (BudgetAddEditDialog.OnInputListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
