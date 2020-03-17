package com.haihoangtran.pm.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.haihoangtran.pm.BudgetActivity;
import com.haihoangtran.pm.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import controller.database.BudgetDB;

public class BudgetYearlyDeleteDialog extends DialogFragment {

    private Spinner yearDropdown;
    private Button cancelBtn, deleteBtn;
    private BudgetDB budgetDB;
    private Context context;

    public BudgetYearlyDeleteDialog(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Define variables
        budgetDB = budgetDB.getInstance(context);

        // Define new view
        View view = inflater.inflate(R.layout.budget_yearly_delete_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        yearDropdown = view.findViewById(R.id.budget_yearly_delete_dialog_year_dropdown);
        cancelBtn = view.findViewById(R.id.budget_yearly_delete_dialog_cancel_btn);
        deleteBtn = view.findViewById(R.id.budget_yearly_delete_dialog_delete_btn);

        // Handle year dropdown
        this.yearDropdown();

        // Handle Cancel button
        this.cancelBtnHandle();

        // Handle Confirm Button
        this.deleteBtnHandle();

        return view;
    }

    /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
    // Handle Yearly Drop down
    private void yearDropdown(){
        List<String> yearItems = new ArrayList<>();
        yearItems = budgetDB.getYears();
        if (yearItems.isEmpty()){
            yearItems.add(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, yearItems);
        adapter.setDropDownViewResource(R.layout.custom_spinner);
        // Apply adapter to spinner
        this.yearDropdown.setAdapter(adapter);
        // Set first selected option
        this.yearDropdown.setSelection(yearItems.size() - 1);
    }

    // Handle Cancel button
    private void cancelBtnHandle(){
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getDialog().dismiss();
            }
        });
    }

    // Handle confirm button
    private void deleteBtnHandle(){
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String selectedYear = yearDropdown.getSelectedItem().toString();
                budgetDB.deleteRecordsByYear(selectedYear);
                getDialog().dismiss();
                Intent budgetIntent = new Intent(context, BudgetActivity.class);
                startActivity(budgetIntent);
            }
        });
    }
}
