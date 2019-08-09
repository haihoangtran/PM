package com.haihoangtran.pm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.haihoangtran.pm.dialogs.PlaceDialog;

import controller.DBController;
import model.PlaceModel;

public class PlaceActivity extends AppCompatActivity implements PlaceDialog.OnInputListener {
    private DBController db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);

        // Create an Back button next to title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Define variables
        db = DBController.getInstance(PlaceActivity.this);

        // Handle Add Btn
        this.addBtnHandle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                Intent intent = new Intent(PlaceActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

     /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
     // --------------           Place DIALOG        ----------
     // Implement SendRecord interface
     // action type: 1 - Add , 2 - Edit
     @Override
     public void sendRecord(int actionType, PlaceModel newPlace, PlaceModel oldPlace){
         switch (actionType) {
             case 1:
                 // Add a new place
                 break;
             case 2:
                 // Update old place by new Place
                 break;
         }
         // Refresh list view
     }

    private void placeDialogHandle(int actionType, PlaceModel place){
        PlaceDialog placeDialog = new PlaceDialog(actionType, place);
        placeDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        placeDialog.show(getSupportFragmentManager(), "PlaceDialog");
    }

    // --------------           TOP NAVIGATION        --------------

    private void addBtnHandle(){
        Button addBtn = findViewById(R.id.place_add_nav_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceModel initialRecord = new PlaceModel(-1, "", "");
                placeDialogHandle(1, initialRecord);

            }
        });
    }

}
