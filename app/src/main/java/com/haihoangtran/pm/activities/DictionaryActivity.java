package com.haihoangtran.pm.activities;

import android.content.Intent;
import android.os.Bundle;
import com.haihoangtran.pm.R;
import com.haihoangtran.pm.dialogs.DictionaryDialog;
import com.haihoangtran.pm.dialogs.DictionaryExistedWordErrorDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import controller.database.DictionaryDB;
import model.DictionaryModel;

public class DictionaryActivity extends AppCompatActivity implements DictionaryDialog.OnInputListener {
    private DictionaryDB dictionaryDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        Toolbar toolbar = findViewById(R.id.dictionary_title);
        setSupportActionBar(toolbar);

        // Initialize dictionary db
        dictionaryDb = DictionaryDB.getInstance(DictionaryActivity.this);

        // Handle dictionary dialog
        this.addBtnHandle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                Intent intent = new Intent(DictionaryActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
    // --------------           Dictionary DIALOG        ----------
    // Implement SendRecord interface
    // action type: 1 - Add , 2 - Edit
    @Override
    public void sendRecord(int actionType, DictionaryModel newDict, DictionaryModel oldDict){
        switch (actionType){
            case 1:
                if (!this.dictionaryDb.addWord(newDict)){
                    System.out.println("Execute here");
                    DictionaryExistedWordErrorDialog errorDialog = new DictionaryExistedWordErrorDialog(newDict);
                    errorDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
                    errorDialog.show(getSupportFragmentManager(), "DictionaryExistedWordErrorDialog");
                };
                break;
            case 2:
                // edit word
                System.out.println("Edit data");
                break;
        }
    }

    public void dictionaryDialogHandle(int actionType, DictionaryModel dictionaryModel){
        DictionaryDialog dictDialog = new DictionaryDialog(actionType, dictionaryModel);
        dictDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        dictDialog.show(getSupportFragmentManager(), "DictionaryDialog");
    }

    // --------------           TOP NAVIGATION        --------------
    public void addBtnHandle(){
        Button addBtn = findViewById(R.id.add_new_word_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DictionaryModel initialDict = new DictionaryModel("");
                dictionaryDialogHandle(1, initialDict);

            }
        });
    }
}
