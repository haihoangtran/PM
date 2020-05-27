package com.haihoangtran.pm.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.R;
import com.haihoangtran.pm.adapters.NoteRecordsAdapter;
import com.haihoangtran.pm.components.SwipeListViewBuilder;
import com.haihoangtran.pm.dialogs.NoteDialog;

import java.util.ArrayList;

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

        // Handle Title List View and Content
        this.titleListViewHandle();
        this.contentHandle("");
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
        this.titleListViewHandle();
        this.contentHandle(newNote.getContent());
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

    // --------------           LIST AND TEXT VIEWS        --------------

    private void titleListViewHandle(){
        SwipeListViewBuilder builder = new SwipeListViewBuilder();
        final ArrayList<NoteModel> noteRecords = noteDb.getAllNotes();
        NoteRecordsAdapter recordDataAdapter = new NoteRecordsAdapter(this, 0, noteRecords);
        SwipeMenuListView recordListView = builder.build(noteRecords, recordDataAdapter,
                                                        (SwipeMenuListView) findViewById(R.id.note_title_list),
                                                        getApplicationContext(),
                                                        15, 180);
        recordListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                // position is location of records list (0:), index is location of 2 buttons.
                switch (index){
                    case 0:
                        noteDialogHandle(2, noteRecords.get(position));
                        break;
                    case 1:
                        noteDb.deleteNote(noteRecords.get(position).getNoteId());
                        titleListViewHandle();
                        contentHandle("");
                        break;
                }
                return false;
            }
        });

        // Add click action on an item
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contentHandle(noteRecords.get(position).getContent());
            }
        });
    }

    private void contentHandle(String content){
        TextView contentText = findViewById(R.id.content_text_view);
        contentText.setText(content);
    }
}
