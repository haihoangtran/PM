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
import model.NoteModel;

public class NoteDialog extends DialogFragment {
    private NoteModel initialNote;
    private TextView dialogTitle;
    private EditText title;
    private EditText content;
    private TextView errorText;
    private Button cancelBtn;
    private Button confirmBtn;
    private int actionType;         // actionType: 1 - Add, 2- Edit

    public interface OnInputListener{
        void sendRecord(int actionType, NoteModel newNote, NoteModel oldNote);
    }

    public NoteDialog.OnInputListener noteInputListener;

    public NoteDialog(int actionType, NoteModel note){
        this.actionType = actionType;
        this.initialNote = note;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.note_add_edit_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        // define elements on dialog
        dialogTitle = view.findViewById(R.id.note_dialog_title);
        title = view.findViewById(R.id.note_title_edit_text);
        content = view.findViewById(R.id.note_content_edit_box);
        errorText = view.findViewById(R.id.note_dialog_error_txt);
        cancelBtn = view.findViewById(R.id.note_add_edit_cancel_btn);
        confirmBtn = view.findViewById(R.id.note_add_edit_confirm_btn);

        // Initialize text for some elements
        dialogTitle.setText(this.actionType == 1 ? getString(R.string.add) : getString(R.string.edit));
        title.setText(initialNote.getTitle());
        content.setText(initialNote.getContent());
        errorText.setVisibility(View.INVISIBLE);

        // Handle onClick of Cancel button
        this.cancelBtnHandle();

        // Handle onClick of Confirm button
        this.confirmBtnHandle();

        return view;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
    // onClick Cancel button
    public void cancelBtnHandle(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    // onClick Confirm button
    public void confirmBtnHandle(){
        confirmBtn.setText((this.actionType == 1 ? getString(R.string.add).toUpperCase() : getString(R.string.edit).toUpperCase()));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = title.getText().toString();
                String newContent = content.getText().toString();
                if (newTitle.matches("") || newContent.matches("")){
                    errorText.setVisibility(View.VISIBLE);
                }else{
                    NoteModel newNote = new NoteModel(initialNote.getNoteId(), newTitle, newContent);
                    NoteModel oldNote = actionType == 1 ? null : initialNote;
                    noteInputListener.sendRecord(actionType, newNote, oldNote);
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        try{
            noteInputListener = (NoteDialog.OnInputListener) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

}