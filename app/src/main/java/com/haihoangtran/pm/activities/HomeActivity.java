package com.haihoangtran.pm.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.content.pm.PackageManager;
import com.haihoangtran.pm.R;
import com.haihoangtran.pm.dialogs.UserDialog;
import com.haihoangtran.pm.dialogs.UserListDialog;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import controller.FileController;
import model.UserModel;
import controller.database.UserDB;
import controller.database.BudgetDB;
import controller.database.PaymentDB;

public class HomeActivity extends AppCompatActivity implements UserDialog.OnInputListener {
    private UserDB userDB;
    private BudgetDB buggetDB;
    private PaymentDB paymentDB;
    private FileController fileController;
    private UserModel currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.title);
        setSupportActionBar(toolbar);

        // Ask Storage Permission
        ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        // Define some variable
        userDB = UserDB.getInstance(HomeActivity.this);
        buggetDB = BudgetDB.getInstance(HomeActivity.this);
        paymentDB = PaymentDB.getInstance(HomeActivity.this);
        fileController = new FileController(HomeActivity.this);
        currentUser = new UserModel(-1, "", 0.0, false);

        // Initialize Payment status
        paymentDB.refreshPaymentMonthlyStatus();

        // Handle user
        this.userHandle();

        // Handle Graph view
        this.graphViewHandle();

        // Handle onClick Budget Management Button
        this.budgetBtnHandle();

        // Handle onClick Payment Button
        this.paymentBtnHandle();

        // Handle onClick Place Button
        this.placeBtnHandle();

        //Handle onClick Note Button
        this.noteBtnHandle();

        // Handle onCLick Vocabulary Button
        this.vocabularyBtnHandle();

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
            case R.id.home_menu_users_btn:
                this.usersListDialog();
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
            case R.id.user_edit_btn:
                this.addEditUserDialog(2, this.currentUser);
                break;
            case R.id.user_delete_btn:
                this.deleteUserBtnHandle();
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
            userDB.addUser(user.getFullName(), user.getBalance(), true);
        }else{
            userDB.updateUser(this.currentUser, user);
        }
        this.currentUser = userDB.getAllSelectedUsers().get(0);
        displayUserInfo();
    }

    private void userHandle(){
        ArrayList<UserModel> selectedUsers = userDB.getAllSelectedUsers();
        ArrayList<UserModel> allUsers = userDB.getAllUsers();
        if (selectedUsers.size() == 0 && allUsers.size() == 0){
            addEditUserDialog(1, new UserModel(-1, "", 0.0, false));
        }else if(selectedUsers.size() > 1 || (selectedUsers.size() == 0 && allUsers.size() > 0)){
            usersListDialog();
        }else{
            this.currentUser = selectedUsers.get(0);
        }
        this.displayUserInfo();
    }

    //Handle dialog of add or edit record
    public void addEditUserDialog(int actionType, UserModel user){
        UserDialog userDialog = new UserDialog(actionType, user, true);
        userDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        userDialog.show(getSupportFragmentManager(), "UserDialog");
    }

    // Handle dialog of users list
    public void usersListDialog(){
        UserListDialog usersListDialog= new UserListDialog(userDB);
        usersListDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_MinWidth);
        usersListDialog.show(getSupportFragmentManager(), "UserListDialog");
    }

    // Display user information
    private void displayUserInfo(){
        TextView name = findViewById(R.id.user_name);
        TextView balance = findViewById(R.id.user_balance);
        TextView expense = findViewById(R.id.monthly_expense);
        TextView deposit = findViewById(R.id.monthly_deposit);
        // Handle User Name
        name.setText(this.currentUser.getFullName());

        // Handle info
        balance.setText(String.format("$%.2f", this.currentUser.getBalance()));
        String[] monthList = getResources().getStringArray(R.array.month_dropdown_items);
        expense.setText(String.format("$%.2f", buggetDB.getMonthlyTotal(monthList[Calendar.getInstance().get(Calendar.MONTH)],
                                                                  Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
                                                                 2)));
        deposit.setText(String.format("$%.2f", buggetDB.getMonthlyTotal(monthList[Calendar.getInstance().get(Calendar.MONTH)],
                Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
                1)));
    }

    // --------------           GRAPH VIEW        --------------
    private void graphViewHandle(){
        String[] monthList = getResources().getStringArray(R.array.month_dropdown_items);

        // Get total amount for 12 months
        List<Double> depositTotals = new ArrayList<Double>(Collections.nCopies(12, 0.00));
        List<Double> withdrawTotals = new ArrayList<Double>(Collections.nCopies(12, 0.00));
        String currentYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        for (int i = 1; i <= 12; ++i){
            depositTotals.set(i -1, buggetDB.getMonthlyTotal(monthList[i-1], currentYear, 1));
            withdrawTotals.set(i -1, buggetDB.getMonthlyTotal(monthList[i-1], currentYear, 2));
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
        graphView.getGridLabelRenderer().setGridColor(ContextCompat.getColor(this, R.color.graphViewColor));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(ContextCompat.getColor(this, R.color.textColor));
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(ContextCompat.getColor(this, R.color.textColor));
        graphView.setTitle(currentYear);
        graphView.setTitleColor(ContextCompat.getColor(this, R.color.textColor));

        // Add linear graph series to graph view
        graphView.addSeries(depositSeries);
        depositSeries.setColor(ContextCompat.getColor(this, R.color.bugetDepositTxt));
        graphView.addSeries(withdrawSeries);
        withdrawSeries.setColor(ContextCompat.getColor(this, R.color.budgetWithdrawTxt));


        // add graph view to linear layout
        LinearLayout dynamicHomeLayout = findViewById(R.id.home_dynamic_layout);
        dynamicHomeLayout.removeAllViews();
        dynamicHomeLayout.addView(graphView);
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
        LinearLayout paymentBtn = findViewById(R.id.payment_btn);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paymentIntent = new Intent(HomeActivity.this, PaymentActivity.class);
                startActivity(paymentIntent);
            }
        });
    }

    //Handle on Click place button
    private void placeBtnHandle(){
        LinearLayout placeBtn = findViewById(R.id.place_btn);
        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent placeIntent = new Intent(HomeActivity.this, PlaceActivity.class);
                startActivity(placeIntent);
            }
        });
    }

    //Handle on click note button
    private void noteBtnHandle(){
        LinearLayout noteBtn = findViewById(R.id.note_btn);
        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noteIntent = new Intent(HomeActivity.this, NoteActivity.class);
                startActivity(noteIntent);
            }
        });
    }

    // Handle onClick Vocabulary button
    private void vocabularyBtnHandle(){
        LinearLayout wordsBtn = findViewById(R.id.dictionary_btn);
        wordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dictionaryIntent = new Intent(HomeActivity.this, DictionaryActivity.class);
                startActivity(dictionaryIntent);
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

    private void deleteUserBtnHandle(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_user_sure_title);
        builder.setMessage(R.string.delete_alert_message);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                userDB.deleteSelectedUser(currentUser.getUserID());
                currentUser = new UserModel(-1, "", 0.0, false);
                displayUserInfo();
                userHandle();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
    }

}
