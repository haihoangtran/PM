package com.haihoangtran.pm.activities;

import android.content.Intent;
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
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.R;
import com.haihoangtran.pm.adapters.PlaceRecordsAdapter;
import com.haihoangtran.pm.components.SwipeListViewBuilder;
import com.haihoangtran.pm.dialogs.PlaceDialog;
import java.util.ArrayList;
import model.PlaceModel;
import  controller.database.PlaceDB;

public class PlaceActivity extends AppCompatActivity implements PlaceDialog.OnInputListener {
    private PlaceDB placeDB;
    private SwipeMenuListView recordListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);


        // Define variables
        placeDB = PlaceDB.getInstance(PlaceActivity.this);

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
                 placeDB.addPlace(newPlace);
                 break;
             case 2:
                 placeDB.updatePlace(newPlace, oldPlace.getPlaceID());
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
        SwipeListViewBuilder builder = new SwipeListViewBuilder();
        final ArrayList<PlaceModel> placeRecords = placeDB.getAllPlaces();

        PlaceRecordsAdapter recordDataAdapter = new PlaceRecordsAdapter(this, 0, placeRecords);
        recordListView = builder.build(placeRecords, recordDataAdapter,
                                      (SwipeMenuListView)findViewById(R.id.place_records_view),
                                       getApplicationContext(), 18, 170);
        recordListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                // position is location of records list (0:), index is location of 2 buttons.
                switch (index){
                    case 0:
                        placeDialogHandle(2, placeRecords.get(position));
                        break;
                    case 1:
                        placeDB.deletePlace(placeRecords.get(position).getPlaceID());
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
