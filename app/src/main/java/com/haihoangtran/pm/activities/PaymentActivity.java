package com.haihoangtran.pm.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.R;
import com.haihoangtran.pm.adapters.PaymentPaidAdapter;
import com.haihoangtran.pm.adapters.PaymentRecordsAdapter;
import com.haihoangtran.pm.dialogs.PaymentDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import model.PaymentModel;
import model.BudgetModel;
import controller.database.PaymentDB;
import controller.database.BudgetDB;

public class PaymentActivity extends NavigationBaseActivity implements PaymentDialog.OnInputListener{
    private PaymentDB paymentDB;
    private BudgetDB budgetDB;
    private SwipeMenuListView recordListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);


        // Define variables
        paymentDB = PaymentDB.getInstance(PaymentActivity.this);
        budgetDB = BudgetDB.getInstance(PaymentActivity.this);

        // Handle add button
        this.addBtnHandle();

        // Handle pay button
        this.payBtnHandle();

        // Handle 2 list vies: details and paid
        paymentDB.refreshPaymentCompleteStatus();
        this.updateListViews();

        //Handle bottom navigation bar
        this.navigationHandle(R.id.nav_payment);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/

    // --------------           PAYMENT DIALOG        ----------
    @Override
    public void sendRecord(int actionType, PaymentModel newRecord, PaymentModel oldRecord, Double paidAmount){
        switch (actionType) {
            case 1:
                paymentDB.addPaymentRecord(newRecord);
                break;
            case 2:
                paymentDB.updatePaymentRecord(newRecord, oldRecord.getPaymentID());
                budgetDB.addBudgetRecord(new BudgetModel(-1, Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), new SimpleDateFormat("MM/dd/yyyy").format(new Date()), newRecord.getPlace(), paidAmount, 2, getString(R.string.withdraw)));
                break;
            case 3:
                paymentDB.updatePaymentRecord(newRecord, oldRecord.getPaymentID());
                break;

        }
        paymentDB.refreshPaymentCompleteStatus();
        this.updateListViews();
    }

    private void paymentDialogHandle(int actionType, PaymentModel record, List<String> placeList){
        PaymentDialog paymentDialog = new PaymentDialog(actionType, record, placeList);
        paymentDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        paymentDialog.show(getSupportFragmentManager(), "PaymentDialog");
    }


    // --------------           TOP NAVIGATION        --------------

    // Add Button Handle
    private void addBtnHandle(){
        Button addBtn = findViewById(R.id.payment_add_nav_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentModel record = new PaymentModel(-1, new SimpleDateFormat("MM/dd/yyyy").format(new Date()), null, 0.00, 0.00, -1,-1,-1);
                List<String> placeList = paymentDB.getAllPaymentPlaces();
                paymentDialogHandle(1, record, placeList);
            }
        });
    }

    // Add Button Handle
    private void payBtnHandle(){
        Button payBtn = findViewById(R.id.pay_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentModel record = new PaymentModel(-1, new SimpleDateFormat("MM/dd/yyyy").format(new Date()), null, 0.00, 0.00, -1,-1,-1);
                List<String> placeList = paymentDB.getPlacesOfIncompletePayment();
                paymentDialogHandle(2, record, placeList);
            }
        });
    }

    // --------------           LIST VIEW        --------------
    // Handle swiping to left for display another button

    private void recordsListViewHandle() {
        final ArrayList<PaymentModel> records = paymentDB.getAllPaymentRecords();

        // Create and Add DataApdater to list view
        recordListView = findViewById(R.id.payment_record_list_view);
        PaymentRecordsAdapter recordDataAdapter = new PaymentRecordsAdapter(this, 0, records);
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
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
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
                switch (index) {
                    case 0:
                        paymentDialogHandle(3, records.get(position), paymentDB.getAllPaymentPlaces());
                        break;
                    case 1:
                        paymentDB.deletePaymentRecord(records.get(position));
                        updateListViews();
                        break;
                }
                return false;
            }
        });
    }

    private void paidListViewHandle(){
        LinearLayout paidLayout = findViewById(R.id.paid_list_layout);;
        final ArrayList<PaymentModel> records = paymentDB.getAllPaymentRecords();
        PaymentPaidAdapter adapter = new PaymentPaidAdapter(this, 0, records);
        //Create a list view
        ListView reminderLv = new ListView(this);
        reminderLv.setId(R.id.paid_list_view);
        reminderLv.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.transparentColor)));
        reminderLv.setVerticalScrollBarEnabled(true);

        reminderLv.setAdapter(adapter);
        paidLayout.removeAllViews();
        paidLayout.addView(reminderLv);
    }

    // Update 2 list vies: paid and details
    private void updateListViews(){
        this.paidListViewHandle();
        this.recordsListViewHandle();
    }
}