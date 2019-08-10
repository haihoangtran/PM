package com.haihoangtran.pm;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.adapters.PaymentRecordsAdapter;
import com.haihoangtran.pm.dialogs.PaymentDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import controller.DBController;
import model.PaymentModel;
import model.BudgetModel;

public class PaymentActivity extends AppCompatActivity implements PaymentDialog.OnInputListener{
    private DBController db;
    private SwipeMenuListView recordListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);

        // Create an Back button next to title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Define variables
        db = DBController.getInstance(PaymentActivity.this);

        // Handle add button
        this.addBtnHandle();

        // Handle pay button
        this.payBtnHandle();

        // Handle Payment record list view
        db.refreshPaymentCompleteStatus();
        this.recordsListViewHandle();

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
                db.addPaymentRecord(newRecord);
                break;
            case 2:
                db.updatePaymentRecord(newRecord, oldRecord.getPaymentID());
                db.addBudgetRecord(new BudgetModel(-1, Integer.toString(Calendar.getInstance().get(Calendar.YEAR)), new SimpleDateFormat("MM/dd/yyyy").format(new Date()), newRecord.getPlace(), paidAmount, 2, getString(R.string.withdraw)));
                break;
            case 3:
                db.updatePaymentRecord(newRecord, oldRecord.getPaymentID());
                break;

        }
        db.refreshPaymentCompleteStatus();
        this.recordsListViewHandle();
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
                List<String> placeList = db.getAllPaymentPlaces();
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
                List<String> placeList = db.getPlacesOfIncompletePayment();
                paymentDialogHandle(2, record, placeList);
            }
        });
    }

    // --------------           LIST VIEW        --------------
    // Handle swiping to left for display another button

    private void recordsListViewHandle() {
        final ArrayList<PaymentModel> records = db.getAllPaymentRecords();

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
                        paymentDialogHandle(3, records.get(position), db.getAllPaymentPlaces());
                        break;
                    case 1:
                        db.deletePaymentRecord(records.get(position));
                        recordsListViewHandle();
                        break;
                }
                return false;
            }
        });
    }
}
