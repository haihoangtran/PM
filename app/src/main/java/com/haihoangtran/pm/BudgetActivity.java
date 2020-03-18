package com.haihoangtran.pm;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import android.widget.AdapterView;
import android.view.View;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.adapters.BudgetRecordsAdapter;
import com.haihoangtran.pm.dialogs.BudgetAddEditDialog;
import com.haihoangtran.pm.dialogs.BudgetYearlyDeleteDialog;

import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import model.BudgetModel;
import controller.database.BudgetDB;


public class BudgetActivity extends NavigationBaseActivity implements BudgetAddEditDialog.OnInputListener{
    private Spinner monthDropdown;
    private Spinner yearDropdown;
    private TabLayout budgetTabLayout;
    private SwipeMenuListView recordListView;
    private BudgetDB budgetDB;
    private int displayType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);


        // Define variables
        budgetDB = budgetDB.getInstance(BudgetActivity.this);

        //Handle Add icon button on navigation
        this.addBtnHandle();

        // Handle Yearly Delete icon button on navigation
        this.yearlyDeleteBtnHandle();

        // Handle month dropdown
        this.monthDropdownHandle();

        // Handle Year dropdown
        this.yearDropdownHandle();

        // Tab handler
        this.budgetTabLayout = findViewById(R.id.budget_tab);
        this.tabHandle();

        // List Record as first display
        this.displayBudgetRecords();

        //Handle bottom navigation bar
        this.navigationHandle(R.id.nav_budget);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                Intent intent = new Intent(BudgetActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/

    // --------------           TOP NAVIGATION        --------------
    // Implement SendRecord interface
    // action type: 1 - Add , 2 - Edit
    @Override
    public void sendRecord(int actionType, BudgetModel newRecord, BudgetModel oldRecord){
        if (actionType == 1){
            budgetDB.addBudgetRecord(newRecord);
            this.yearDropdownHandle();
        }else{
            budgetDB.updateRecord(newRecord, oldRecord);
            this.displayBudgetRecords();
        }
    }

    //Handle onClick Add icon button
    private void addBtnHandle(){
        Button addBtn = findViewById(R.id.budget_add_nav_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BudgetModel initialRecord = new BudgetModel(-1, Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), new SimpleDateFormat("MM/dd/yyyy").format(new Date()),null, 0.0, 1, null);
                addEditDialogHandle(1, initialRecord);

            }
        });
    }
    //Handle dialog of add or edit record
    private void addEditDialogHandle(int actionType, BudgetModel record){
        BudgetAddEditDialog budgetAddEditDialog = new BudgetAddEditDialog(actionType, record);
        budgetAddEditDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        budgetAddEditDialog.show(getSupportFragmentManager(), "BudgetAddEditDialog");
    }

    // Handle Yearly Delete button
    private void yearlyDeleteBtnHandle(){
        Button yearlyDeleteBtn = findViewById(R.id.budget_yearly_delete_nav_btn);
        yearlyDeleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                BudgetYearlyDeleteDialog deleteDialog = new BudgetYearlyDeleteDialog(BudgetActivity.this);
                deleteDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
                deleteDialog.show(getSupportFragmentManager(), "BudgetYearlyDeleteDialog");
            }
        });
    }

    // --------------           DROPDOWN BUTTONS and TOTAL Text        --------------
    private void monthDropdownHandle(){
        this.monthDropdown = (Spinner)findViewById(R.id.month_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.month_dropdown_items,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner);
        // Apply adapter to spinner
        this.monthDropdown.setAdapter(adapter);
        // Set first selected option
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        this.monthDropdown.setSelection(calendar.get(Calendar.MONTH));
        // handle on selected items of month spinner
        this.monthDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                displayBudgetRecords();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void yearDropdownHandle(){
        this.yearDropdown = findViewById(R.id.year_dropdown);
        List<String> yearItems = new ArrayList<>();
        yearItems = budgetDB.getYears();
        if (yearItems.isEmpty()){
            yearItems.add(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearItems);
        adapter.setDropDownViewResource(R.layout.custom_spinner);
        // Apply adapter to spinner
        this.yearDropdown.setAdapter(adapter);
        // Set first selected option
        this.yearDropdown.setSelection(yearItems.size() - 1);
        this.yearDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                displayBudgetRecords();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // --------------           TAB BAR        --------------
    private void tabHandle(){
        this.budgetTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                displayType = tab.getPosition();
                displayBudgetRecords();
            }

            @Override
            public void onTabUnselected(Tab tab) {}

            @Override
            public void onTabReselected(Tab tab) {}
        });
    }

    // --------------           LIST VIEW        --------------
    /*
    Handle for swiping for displaying another button
     */
    private void recordsListViewHandle(){

        final ArrayList<BudgetModel> records = budgetDB.getMonthlyRecords(this.monthDropdown.getSelectedItem().toString(),
                                                              this.yearDropdown.getSelectedItem().toString(),
                                                              this.displayType);

        // Create and Add DataApdater to list view
        recordListView = findViewById(R.id.record_list_view);
        BudgetRecordsAdapter recordDataAdapter = new BudgetRecordsAdapter(this, 0, records);
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
                        addEditDialogHandle(2, records.get(position));
                        break;
                    case 1:
                        budgetDB.deleteRecord(records.get(position));
                        displayBudgetRecords();
                        break;
                }
                return false;
            }
        });

    }

    // --------------           MISC        --------------
    private void displayBudgetRecords(){
        TextView monthlyTotal = findViewById(R.id.monthly_total_text);
        Double total = 0.0;
        if (this.displayType != 0){
            total = budgetDB.getMonthlyTotal(this.monthDropdown.getSelectedItem().toString(),
                    this.yearDropdown.getSelectedItem().toString(),
                    this.displayType);
            monthlyTotal.setText(String.format("$%.2f", total));
        }else{
            monthlyTotal.setText("");
        }
        if(this.displayType == 1){
            monthlyTotal.setTextColor(ContextCompat.getColor(BudgetActivity.this, R.color.bugetDepositTxt));
        } else if(this.displayType == 2){
            monthlyTotal.setTextColor(ContextCompat.getColor(BudgetActivity.this, R.color.budgetWithdrawTxt));
        }
        this.recordsListViewHandle();
    }
}
