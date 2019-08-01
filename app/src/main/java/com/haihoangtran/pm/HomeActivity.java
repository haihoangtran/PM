package com.haihoangtran.pm;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import android.Manifest;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.content.pm.PackageManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import controller.DBController;
import controller.FileController;
import model.PaymentModel;
import model.UserModel;

public class HomeActivity extends AppCompatActivity implements UserDialog.OnInputListener {
    private DBController db;
    private FileController fileController;
    private UserModel currentUser;
    private TabLayout homeTabLayout;
    private LinearLayout dynamicHomeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);

        // Ask for Storage Permission
        // Ask Storage Permission
        ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        // Define some variable
        db = DBController.getInstance(HomeActivity.this);
        fileController = new FileController(HomeActivity.this);
        currentUser = new UserModel("", 0.0);

        // Initialize Payment status
        db.refreshPaymentMonthlyStatus();

        // Handle user
        this.userHandle();

        // handle Home Tab Layout
        this.homeTabLayout = findViewById(R.id.home_tab_layout);
        this.dynamicHomeLayout = findViewById(R.id.home_dynamic_layout);
        this.tabHandle();


        // Handle onClick Budget Management Button
        this.budgetBtnHandle();

        // Handle onClick PaymentModel Button
        this.paymentBtnHandle();

    }

    // Handle App Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]grantResult){
        switch(requestCode){
            case 1:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){
                    System.out.println("Permission is granted");
                }
                else{
                    Toast toast_msg = Toast.makeText(getApplicationContext(),
                            getString(R.string.app_permission_denied),
                            Toast.LENGTH_SHORT);
                    toast_msg.setGravity(Gravity.CENTER, 0, 0);
                    toast_msg.show();
                }
        }
    }

    // Menu option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.home_menu_edit_btn:
                this.addEditUserDialog(2, this.currentUser);
                break;
            case R.id.home_menu_export_btn:
                this.fileController.exportDBToLocalStorage();
                break;
            case R.id.home_menu_import_btn:
                try {
                    this.fileController.importDBFromLocalStorage();
                    finish();
                    startActivity(getIntent());
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                break;
            case R.id.home_menu_share_btn:
                this.shareBtnHandle();
                break;
        }
        return true;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/

    // --------------           USER        --------------

    // Implement SendUser interface
    // action type: 1 - Add , 2 - Edit
    @Override
    public void sendUser(int actionType, UserModel user){
        if (actionType == 1){
            db.addUser(user.getFullName(), user.getBalance());
        }else{
            db.updateUser(this.currentUser, user);
        }
        this.currentUser = user;
        displayUserInfo();
    }

    private void userHandle(){
        ArrayList<UserModel> users = db.getUser();
        if (users.size() < 1){
            addEditUserDialog(1, new UserModel("", 0.0));
        }else{
            this.currentUser = users.get(0);
        }
        this.displayUserInfo();
    }

    //Handle dialog of add or edit record
    public void addEditUserDialog(int actionType, UserModel user){
        UserDialog userDialog = new UserDialog(actionType, user);
        userDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        userDialog.show(getSupportFragmentManager(), "UserDialog");
    }

    // Display user information
    private void displayUserInfo(){
        TextView name = findViewById(R.id.user_name);
        TextView balance = findViewById(R.id.user_balance);
        TextView expense = findViewById(R.id.user_expense);
        // Handle User Name
        name.setText(this.currentUser.getFullName());

        // Handle info
        balance.setText(String.format("$%.2f", this.currentUser.getBalance()));
        String[] monthList = getResources().getStringArray(R.array.month_dropdown_items);
        expense.setText(String.format("$%.2f", db.getMonthlyTotal(monthList[Calendar.getInstance().get(Calendar.MONTH)],
                                                                  Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
                                                                 2)));
    }

    // --------------           TAB BAR        --------------
    private void tabHandle(){
        graphViewHandle();
        this.homeTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        graphViewHandle();
                        break;
                    case 1:
                        reminderListViewHandle();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // --------------           GRAPH VIEW        --------------
    private void graphViewHandle(){
        String[] monthList = getResources().getStringArray(R.array.month_dropdown_items);

        // Get total amount for 12 months
        List<Double> depositTotals = new ArrayList<Double>(Collections.nCopies(12, 0.00));
        List<Double> withdrawTotals = new ArrayList<Double>(Collections.nCopies(12, 0.00));
        String currentYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        for (int i = 1; i <= 12; ++i){
            depositTotals.set(i -1, db.getMonthlyTotal(monthList[i-1], currentYear, 1));
            withdrawTotals.set(i -1, db.getMonthlyTotal(monthList[i-1], currentYear, 2));
        }

        // Create Data Point and Linear Graph Series
        DataPoint [] depositDataPoints = new DataPoint[12];
        for (int i = 1; i <=depositTotals.size(); ++i){
            depositDataPoints[i-1] = new DataPoint(i, depositTotals.get(i-1));
        }
        DataPoint [] withdrawDataPoints = new DataPoint[12];
        for (int i = 1; i <=withdrawTotals.size(); ++i){
            withdrawDataPoints[i-1] = new DataPoint(i, withdrawTotals.get(i-1));
        }

        LineGraphSeries<DataPoint> depositSeries = new LineGraphSeries<>(depositDataPoints);
        LineGraphSeries<DataPoint> withdrawSeries = new LineGraphSeries<>(withdrawDataPoints);


        // Create Graph view variable
        GraphView graphView = new GraphView(this);
        graphView.setId(R.id.home_graph);
        graphView.removeAllSeries();
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(1);
        graphView.getViewport().setMaxX(12);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(12);
        graphView.setTitle(currentYear);

        // Add linear graph series to graph view
        graphView.addSeries(depositSeries);
        depositSeries.setColor(ContextCompat.getColor(this, R.color.bugetDepositTxt));
        graphView.addSeries(withdrawSeries);
        withdrawSeries.setColor(ContextCompat.getColor(this, R.color.budgetWithdrawTxt));


        // add graph view to linear layout
        this.dynamicHomeLayout.removeAllViews();
        this.dynamicHomeLayout.addView(graphView);
    }

    // --------------           REMINDER VIEW       --------------
    private void reminderListViewHandle() {

        final ArrayList<PaymentModel> records = db.getAllPaymentRecords();
        HomeReminderAdapter adapter = new HomeReminderAdapter(this, 0, records);
        //Create a list view
        ListView reminderLv = new ListView(this);
        reminderLv.setId(R.id.reminder_list_view);
        reminderLv.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.transparentColor)));
        reminderLv.setVerticalScrollBarEnabled(true);

        reminderLv.setAdapter(adapter);
        this.dynamicHomeLayout.removeAllViews();
        this.dynamicHomeLayout.addView(reminderLv);
    }

    // --------------           BOTTOM BUTTONS        --------------
    //Handle onClick Budget  button
    private void budgetBtnHandle(){
        LinearLayout budgetBtn = findViewById(R.id.budget_btn);
        budgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent budgetIntent = new Intent(HomeActivity.this, BudgetActivity.class);
                startActivity(budgetIntent);
            }
        });
    }

    // Handle onClick payment button
    private void paymentBtnHandle(){
        LinearLayout paymentBtn =findViewById(R.id.payment_btn);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent budgetIntent = new Intent(HomeActivity.this, PaymentActivity.class);
                startActivity(budgetIntent);
            }
        });
    }

    // --------------           MENU BUTTONS        --------------
    private void shareBtnHandle(){
        // Need 2 updates in 2 files for sharing with File Provider: Manifest, resource/cml/provider_paths.xml
        File dbFile = this.fileController.getDBFile();
        Uri path = FileProvider.getUriForFile(this, "com.haihoangtran.pm", dbFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "It is db file");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("application/octet-stream");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_using_title)));
    }


}
