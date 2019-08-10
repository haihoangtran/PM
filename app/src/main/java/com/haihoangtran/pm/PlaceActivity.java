package com.haihoangtran.pm;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.adapters.PlaceRecordsAdapter;
import com.haihoangtran.pm.dialogs.PlaceDialog;

import java.util.ArrayList;

import controller.DBController;
import model.PlaceModel;

public class PlaceActivity extends AppCompatActivity implements PlaceDialog.OnInputListener {
    private DBController db;
    private SwipeMenuListView recordListView;

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

        // Handle Place list view
        this.recordsListViewHandle();
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
                 db.addPlace(newPlace);
                 break;
             case 2:
                 db.updatePlace(newPlace, oldPlace.getPlaceID());
                 break;
         }

         // Display current address and refresh list view
         this.recordsListViewHandle();
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

    // --------------          Place List View     --------------
    private void recordsListViewHandle(){

        final ArrayList<PlaceModel> placeRecords = db.getAllPlaces();

        // Create and Add DataApdater to list view
        recordListView = findViewById(R.id.place_records_view);
        PlaceRecordsAdapter recordDataAdapter = new PlaceRecordsAdapter(this, 0, placeRecords);
        recordListView.setAdapter(recordDataAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // Create Edit button
                SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1, 0xF5)));
                editItem.setWidth(170);
                editItem.setTitle(R.string.edit);
                editItem.setTitleSize(18);
                editItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }

        };

        // Add menu item and handle action on menu items
        recordListView.setMenuCreator(creator);
        recordListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                // position is location of records list (0:), index is location of 2 buttons.
                switch (index){
                    case 0:
                        placeDialogHandle(2, placeRecords.get(position));
                        break;
                    case 1:
                        db.deletePlace(placeRecords.get(position).getPlaceID());
                        recordsListViewHandle();
                        break;
                }
                return false;
            }
        });

        // Add click action on an item
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open google map app after clicking on item
                String selectedAddr = placeRecords.get(position).getAddress();
                Uri adrUri = Uri.parse("geo:0,0?q= " + selectedAddr);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, adrUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }
}