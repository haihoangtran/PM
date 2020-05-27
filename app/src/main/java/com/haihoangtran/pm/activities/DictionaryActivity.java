package com.haihoangtran.pm.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.R;
import com.haihoangtran.pm.adapters.DictionaryWordRecordsAdapter;
import com.haihoangtran.pm.components.SwipeListViewBuilder;
import com.haihoangtran.pm.dialogs.DictionaryDialog;
import com.haihoangtran.pm.dialogs.DictionaryExistedWordErrorDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import controller.database.DictionaryDB;
import model.DictionaryModel;

public class DictionaryActivity extends AppCompatActivity implements DictionaryDialog.OnInputListener {
    private DictionaryDB dictionaryDb;
    private String searchValue = "";

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

        // Handle search Btn
        this.searchBtnHandle();

        // Handle List View of Word Result Record
        this.searchResultListViewHandle();
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
    // --------------           DICTIONARY DIALOG        ----------
    // Implement SendRecord interface
    // action type: 1 - Add , 2 - Edit
    @Override
    public void sendRecord(int actionType, DictionaryModel newDict, DictionaryModel oldDict){
        boolean isSuccessful = false;
        switch (actionType){
            case 1:
                isSuccessful = this.dictionaryDb.addWord(newDict);
                searchValue = "";       // display all value to result list view after adding new word
                break;
            case 2:
                // edit word
                isSuccessful = dictionaryDb.updateWord(newDict, oldDict);
                break;
        }
        if (!isSuccessful){
            DictionaryExistedWordErrorDialog errorDialog = new DictionaryExistedWordErrorDialog(newDict);
            errorDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
            errorDialog.show(getSupportFragmentManager(), "DictionaryExistedWordErrorDialog");
        }else {
            // Refresh result list view
            searchResultListViewHandle();
        }
        webviewHandle("");
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

    // --------------           SEARCH       --------------
    public void searchBtnHandle(){
        Button searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText keyword = findViewById(R.id.dictionary_search_edit_text);
                searchValue = keyword.getText().toString();
                searchResultListViewHandle();
        }});

    }

    // --------------           LIST VIEW        --------------
    public void searchResultListViewHandle(){
        SwipeListViewBuilder builder = new SwipeListViewBuilder();
        final ArrayList<DictionaryModel> wordRecords = searchValue.matches("") ? dictionaryDb.getAllWords(): dictionaryDb.getWordsByKeyWord(searchValue);
        DictionaryWordRecordsAdapter recordDataAdapter = new DictionaryWordRecordsAdapter(this, 0, wordRecords);
        SwipeMenuListView recordListView = builder.build(wordRecords,recordDataAdapter,
                                                        (SwipeMenuListView) findViewById(R.id.dictionary_result_search),
                                                         getApplicationContext(), 15, 180);
        recordListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                // position is location of records list (0:), index is location of 2 buttons.
                switch (index){
                    case 0:
                        dictionaryDialogHandle(2, wordRecords.get(position));
                        break;
                    case 1:
                        dictionaryDb.deleteWord(wordRecords.get(position).getWord());
                        break;
                }
                // Refresh List  View for each action Delete/Edit
                searchResultListViewHandle();
                return false;
            }
        });

        // Add click action on an item
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                webviewHandle(wordRecords.get(position).getWord());
            }
        });
    }

    // --------------           WEB VIEW FOR GOOGLE SEARCH        --------------
    public void webviewHandle(String query){
        WebView wView = findViewById(R.id.word_mean_webview);
        wView.getSettings().setJavaScriptEnabled(true);
        String url = "about:blank";
        if (!query.matches("")){
            url = "https://www.google.com/search?q=" + query + "+meaning";
        }
        wView.loadUrl(url);
    }
}
