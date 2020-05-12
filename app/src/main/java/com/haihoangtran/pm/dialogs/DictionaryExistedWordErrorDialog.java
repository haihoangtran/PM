package com.haihoangtran.pm.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.haihoangtran.pm.R;

import model.DictionaryModel;

public class DictionaryExistedWordErrorDialog extends DialogFragment {
    private DictionaryModel dictionary;
    private TextView errorMsg;
    private Button okBtn;

    public DictionaryExistedWordErrorDialog(DictionaryModel dictionary) {
        this.dictionary = dictionary;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.existed_word_error_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        // Define elements
        errorMsg = view.findViewById(R.id.existed_word_error_dialog_msg);
        okBtn = view.findViewById(R.id.existed_word_error_dialog_ok_btn);

        // Initialize value for some element
        errorMsg.setText(String.format(getString(R.string.existed_word_error_dialog_message), this.dictionary.getWord()));

        // Handle click action for OK button
        this.okBtnHandle();

        return view;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
    private void okBtnHandle(){
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}
