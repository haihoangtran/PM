package com.haihoangtran.pm.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.haihoangtran.pm.R;

import model.PlaceModel;

public class PlaceDialog extends DialogFragment {
    private PlaceModel initialPlace;
    private TextView title, errorTxt;
    private EditText name, address;
    private Button cancelBtn, confirmBtn;
    private int actionType;

    public interface OnInputListener{
        void sendRecord(int actionType, PlaceModel newPlace, PlaceModel oldPlace);
    }

    public PlaceDialog.OnInputListener recordInputListener;

    public PlaceDialog(int actionType, PlaceModel place){
        this.actionType = actionType;
        this.initialPlace = place;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_add_edit_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        title = view.findViewById(R.id.dialog_title);
        errorTxt = view.findViewById(R.id.place_add_edit_dialog_err_text);
        name = view.findViewById(R.id.place_name_edit_text);
        address = view.findViewById(R.id.place_address_edit_Text);
        cancelBtn = view.findViewById(R.id.place_add_edit_diallog_cancel);
        confirmBtn = view.findViewById(R.id.place_add_edit_confirm);

        // Initialize text for some elements
        title.setText((this.actionType == 1 ? getString(R.string.add) : getString(R.string.edit)));
        errorTxt.setVisibility(View.INVISIBLE);
        name.setText(initialPlace.getName());
        address.setText(initialPlace.getAddress());


        // Handle Cancel button
        this.cancelBtnHandle();

        // Handle Confirm Button
        this.confirmBtnHandle();

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

    // Handle confirm button
    private void confirmBtnHandle(){
        confirmBtn.setText((this.actionType == 1 ? getString(R.string.add).toUpperCase() : getString(R.string.edit).toUpperCase()));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place_name = name.getText().toString();
                String place_address = address.getText().toString();
                if (place_name.matches("") || place_address.matches("")){
                    errorTxt.setVisibility(View.VISIBLE);
                }else {
                    PlaceModel newPlace = new PlaceModel(initialPlace.getPlaceID(), place_name, place_address);
                    if (actionType == 1) {
                        recordInputListener.sendRecord(actionType, newPlace, null);
                    }else{
                        recordInputListener.sendRecord(actionType, newPlace, initialPlace);
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
            recordInputListener = (PlaceDialog.OnInputListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
