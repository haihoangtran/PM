package com.haihoangtran.pm.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.haihoangtran.pm.R;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import model.PaymentModel;
import controller.DBController;

public class PaymentDialog extends DialogFragment {
    private TextView title;
    private Spinner toSpinner;
    private LinearLayout dateLayout;
    private EditText date;
    private LinearLayout totalLayout;
    private EditText totalAmount;
    private LinearLayout defaultLayout;
    private EditText defaultAmount;
    private EditText paidTo;
    private LinearLayout payTypeLayout;
    private CheckBox defaultCb;
    private CheckBox customCb;
    private EditText customAmount;
    private TextView errorMsg;
    private Button cancelBtn;
    private Button confirmBtn;
    private int actionType;
    private PaymentModel paymentRecord;
    private List<String> placeList;


    public interface OnInputListener{
        void sendRecord(int actionType, PaymentModel record, PaymentModel oldRecord, Double paidAmount);
    }

    public PaymentDialog.OnInputListener recordInputListener;

    public PaymentDialog(int actionType, PaymentModel paymentRecord, List<String> placeList){
        // actionType: 1 - Add, 2 - Pay, 3- Edit
        this.actionType = actionType;
        this.paymentRecord = paymentRecord;
        this.placeList = placeList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        title = view.findViewById(R.id.payment_dialog_title);
        toSpinner = view.findViewById(R.id.pay_to_spinner);
        dateLayout = view.findViewById(R.id.date_layout);
        date = view.findViewById(R.id.date_edit_text);
        totalLayout = view.findViewById(R.id.total_layout);
        totalAmount = view.findViewById(R.id.total_amount_edit_text);
        defaultLayout = view.findViewById(R.id.default_layout);
        defaultAmount = view.findViewById(R.id.default_amount_edit_text);
        paidTo = view.findViewById(R.id.pay_to_edit_text);
        payTypeLayout = view.findViewById(R.id.pay_type);
        defaultCb = view.findViewById(R.id.default_amount_cb);
        customCb = view.findViewById(R.id.custom_amount_cb);
        customAmount = view.findViewById(R.id.custom_amount_edit_text);
        errorMsg = view.findViewById(R.id.error_msg_text);
        cancelBtn = view.findViewById(R.id.payment_add_edi_cancel_btn);
        confirmBtn = view.findViewById(R.id.payment_add_edit_confirm_btn);

        // Initialize dialog
        this.initializeDialog();

        // Handle Cancel button
        this.cancelBtnHandle();

        //Handle Confirm Button
        this.confirmBtnHandle();

        return view;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/

    // Handle initialize dialog based on action type
    private void initializeDialog(){
        this.errorMsg.setVisibility(View.INVISIBLE);
        // actionType: 1 - Add, 2 - Pay, 3- Edit
        switch(this.actionType){
            case 1:
                this.placeList.add(0, getString(R.string.new_payment));
                title.setText(getString(R.string.add));
                this.toSpinnerHandle();
                if (this.toSpinner.getSelectedItem().toString().equals(getString(R.string.new_payment)))
                    this.paidTo.setVisibility(View.VISIBLE);
                else
                    this.paidTo.setVisibility(View.INVISIBLE);
                this.date.setText(this.paymentRecord.getDate());
                this.payTypeLayout.setVisibility(View.INVISIBLE);
                this.customAmount.setVisibility(View.INVISIBLE);
                break;

            case 2:
                title.setText(getString(R.string.pay));
                //handle spinner
                if (this.placeList.isEmpty()){
                    this.toSpinner.setEnabled(false);
                }else {
                    this.toSpinnerHandle();
                }

                this.dateLayout.setVisibility(View.INVISIBLE);
                //Handle total and default edit text for showing informationonly
                this.totalAmount.setText(String.format("%.2f", this.paymentRecord.getTotalAmount()));
                this.totalAmount.setInputType(InputType.TYPE_NULL);
                this.defaultAmount.setText(String.format("%.2f", this.paymentRecord.getDefaultAmount()));
                this.defaultAmount.setInputType(InputType.TYPE_NULL);

                this.paidTo.setVisibility(View.INVISIBLE);
                this.defaultCb.setChecked(true);
                this.checkBoxHandle(1);         // handle default cb
                this.checkBoxHandle(2);         // handle custom cb
                this.customAmount.setVisibility(View.INVISIBLE);
                break;

            case 3:
                title.setText(getString(R.string.edit).toUpperCase());
                this.toSpinner.setVisibility(View.INVISIBLE);
                paidTo.setText(this.paymentRecord.getPlace());
                date.setText(this.paymentRecord.getDate());
                totalAmount.setText(String.format("%.2f", this.paymentRecord.getTotalAmount()));
                defaultAmount.setText(String.format("%.2f", this.paymentRecord.getDefaultAmount()));
                payTypeLayout.setVisibility(View.INVISIBLE);
                customAmount.setVisibility(View.INVISIBLE);


                break;
        }
    }

    // Handle toSpinner
    private void toSpinnerHandle(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, this.placeList);
        adapter.setDropDownViewResource(R.layout.custom_spinner);
        this.toSpinner.setAdapter(adapter);
        if (this.actionType == 1)
            this.toSpinner.setSelection(0);
        this.toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getActivity(), R.color.textColor));
                if (actionType == 1 && placeList.get(position).equals(getString(R.string.new_payment))) {
                    paidTo.setVisibility(View.VISIBLE);
                    paidTo.setText("");
                }
                else {
                    paidTo.setVisibility(View.INVISIBLE);
                }
                if(actionType == 2){
                    DBController db = DBController.getInstance(getActivity());
                    paymentRecord = db.getPaymentRecodsByPlace(placeList.get(position)).get(0);
                    totalAmount.setText(String.format("%.2f", paymentRecord.getTotalAmount()));
                    defaultAmount.setText(String.format("%.2f", paymentRecord.getDefaultAmount()));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // handle Checkbox btns
    private void checkBoxHandle(int payType){
        // payType: 1 - default amount 2 - custom amount
        switch(payType) {
            case 1:
                defaultCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultCb.setChecked(true);
                        customCb.setChecked(false);
                        customAmount.setVisibility(View.INVISIBLE);
                    }
                });
                break;
            case 2:
                customCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultCb.setChecked(false);
                        customCb.setChecked(true);
                        customAmount.setVisibility(View.VISIBLE);
                        customAmount.setText("");
                    }
                });
                break;

        }
    }

    // Handle Cancel button
    private void cancelBtnHandle(){
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getDialog().dismiss();
            }
        });
    }

    // Handle Confirm button
    private void confirmBtnHandle(){
        switch (this.actionType){
            case 1:
                confirmBtn.setText(getString(R.string.add).toUpperCase());
                break;
            case 2:
                confirmBtn.setText(getString(R.string.pay).toUpperCase());
                break;
            case 3:
                confirmBtn.setText(getString(R.string.edit).toUpperCase());
                break;
        }
        confirmBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                PaymentModel newRecord = null;
                String record_place = "", record_date = "";
                Double record_total = 0.00,  record_default = 0.00, custom_amount = 0.00;
                switch (actionType) {
                    case 1:
                        String newPlace = "";
                        int monthStatus = 0;
                        int currentMonth = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH) + 1;
                        if (toSpinner.getSelectedItem().toString().equals(getString(R.string.new_payment))) {
                            newPlace = paidTo.getText().toString();
                        }else {
                            newPlace = toSpinner.getSelectedItem().toString();
                        }
                        record_date = date.getText().toString();
                        record_total = ((totalAmount.getText().toString().matches("")) ? 0.00 : Double.parseDouble(totalAmount.getText().toString()));
                        record_default = ((defaultAmount.getText().toString().matches("")) ? 0.00 : Double.parseDouble(defaultAmount.getText().toString()));
                        if (newPlace.matches("") || record_date.matches("") || record_total == 0.00 || record_default == 0.00) {
                            errorMsg.setVisibility(View.VISIBLE);
                        } else{
                            newRecord = new PaymentModel(-1, record_date, newPlace, record_total, record_default, monthStatus, currentMonth, 0);
                            recordInputListener.sendRecord(1, newRecord, paymentRecord, 0.00);
                            getDialog().dismiss();
                        }
                        break;
                    case 2:
                        record_place = ((toSpinner.isEnabled()) ? toSpinner.getSelectedItem().toString() : "");
                        record_date = paymentRecord.getDate();
                        record_total = paymentRecord.getTotalAmount();
                        record_default = paymentRecord.getDefaultAmount();
                        if (defaultCb.isChecked()){
                            custom_amount = record_default;
                        }else{
                            custom_amount = ((customAmount.getText().toString().matches("")) ? 0.00 : Double.parseDouble(customAmount.getText().toString()));
                        }
                        custom_amount = ((custom_amount < record_total) ? custom_amount : record_total);
                        if (custom_amount  == 0.00 || record_place.matches("")) {
                            errorMsg.setVisibility(View.VISIBLE);
                        } else{
                            newRecord = new PaymentModel(paymentRecord.getPaymentID(), record_date, record_place, record_total - custom_amount, record_default, 1, paymentRecord.getCurrentMonth(), paymentRecord.getCompleted());
                            recordInputListener.sendRecord(2, newRecord, paymentRecord, custom_amount);
                            getDialog().dismiss();
                        }
                        break;
                    case 3:
                        record_place = paidTo.getText().toString();
                        record_date = date.getText().toString();
                        record_total = ((totalAmount.getText().toString().matches("")) ? 0.00 : Double.parseDouble(totalAmount.getText().toString()));
                        record_default = ((defaultAmount.getText().toString().matches("")) ? 0.00 : Double.parseDouble(defaultAmount.getText().toString()));

                        if (record_place.matches("") || record_date.matches("")) {
                            errorMsg.setVisibility(View.VISIBLE);
                        } else{
                            newRecord = new PaymentModel(paymentRecord.getPaymentID(), record_date, record_place, record_total, record_default, paymentRecord.getMonthStatus(), paymentRecord.getCurrentMonth(), paymentRecord.getCompleted());
                            recordInputListener.sendRecord(3, newRecord, paymentRecord, 0.00);
                            getDialog().dismiss();
                        }
                        break;
                }

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            recordInputListener = (PaymentDialog.OnInputListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
