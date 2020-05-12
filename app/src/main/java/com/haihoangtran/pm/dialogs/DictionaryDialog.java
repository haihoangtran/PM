package com.haihoangtran.pm.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.haihoangtran.pm.R;
import model.DictionaryModel;

public class DictionaryDialog extends DialogFragment {
    private DictionaryModel initialDict;
    private TextView dialogTitle;
    private EditText word;
    private TextView errorTxt;
    private Button cancelBtn;
    private Button confirmBtn;
    private int actionType;             // actionType: 1 - Add, 2- Edit

    public interface OnInputListener{
        void sendRecord(int actionType, DictionaryModel newDict, DictionaryModel oldDict);
    }

    public DictionaryDialog.OnInputListener dictionaryInputListener;

    public DictionaryDialog(int actionType, DictionaryModel dictionary){
        this.actionType = actionType;
        this.initialDict = dictionary;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.words_add_edit_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        // Define elements in dialog
        dialogTitle = view.findViewById(R.id.words_dialog_title);
        word = view.findViewById(R.id.word_edit_text);
        errorTxt = view.findViewById(R.id.dictionary_dialog_error_txt);
        cancelBtn = view.findViewById(R.id.words_add_edit_dialog_cancel);
        confirmBtn = view.findViewById(R.id.words_add_edit_dialog_confirm);

        // Initialize value for some elements
        dialogTitle.setText(this.actionType == 1 ? getString(R.string.add) : getString(R.string.edit));
        word.setText(initialDict.getWord());
        errorTxt.setVisibility(View.INVISIBLE);

        // Handle click action on 2 buttons
        this.cancelBtnHandle();
        this.confirmBtnHandle();

        return view;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
    private void cancelBtnHandle(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void confirmBtnHandle(){
        confirmBtn.setText((this.actionType == 1 ? getString(R.string.add).toUpperCase() : getString(R.string.edit).toUpperCase()));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String newWord = word.getText().toString();
            if (newWord.matches("")){
                errorTxt.setVisibility(View.VISIBLE);
            }else{
                DictionaryModel newDict = new DictionaryModel(newWord);
                DictionaryModel oldDict = actionType == 1 ? null : initialDict;
                dictionaryInputListener.sendRecord(actionType, newDict, oldDict);
                getDialog().dismiss();
            }
            }
        });
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
             dictionaryInputListener = (DictionaryDialog.OnInputListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
