package com.haihoangtran.pm;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.haihoangtran.pm.dialogs.NoteDialog;

import controller.database.NoteDB;
import model.NoteModel;

public class NoteActivity extends AppCompatActivity implements NoteDialog.OnInputListener {
    private NoteDB noteDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);

        // initial Note DB
        noteDb = NoteDB.getInstance(NoteActivity.this);

        // Handle onClick of Add button
        this.addBtnHandle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                Intent intent = new Intent(NoteActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
    // --------------           Note DIALOG        ----------
    // Implement SendRecord interface
    // action type: 1 - Add , 2 - Edit
    @Override
    public void sendRecord(int actionType, NoteModel newNote, NoteModel oldNote){
        switch (actionType) {
            case 1:
                noteDb.addNote(newNote);
                break;
            case 2:
                noteDb.updateNote(newNote, oldNote.getNoteId());
                break;
        }

        // Display current address and refresh list view
//        this.recordsListViewHandle();
    }

    private void noteDialogHandle(int actionType, NoteModel note){
        NoteDialog noteDialog = new NoteDialog(actionType, note);
        noteDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        noteDialog.show(getSupportFragmentManager(), "NoteDialog");
    }

    // --------------           TOP NAVIGATION        --------------

    private void addBtnHandle(){
        Button addBtn = findViewById(R.id.note_add_nav_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteModel initialNote = new NoteModel(-1, "", "");
                noteDialogHandle(1, initialNote);

            }
        });
    }
}
