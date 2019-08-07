package com.haihoangtran.pm.adapters;


import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.haihoangtran.pm.R;

import model.RecordModel;

public class BudgetRecordsAdapter extends ArrayAdapter<RecordModel> {
    private Context context;
    private List<RecordModel> records;

    public BudgetRecordsAdapter(Context context, int resource, ArrayList<RecordModel> records){
        super(context, resource, records);
        this.context = context;
        this.records = records;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RecordModel record = records.get(position);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.budget_record_cell, null);

        // Lookup view for data population
        TextView placeTxt = (TextView) view.findViewById(R.id.place_txt);
        TextView dateTxt = (TextView) view.findViewById(R.id.date_txt);
        TextView typeTxt = (TextView) view.findViewById(R.id.type_txt);
        TextView amountTxt = (TextView) view.findViewById(R.id.amount_txt);

        // Populate the data into the template view using the data object
        placeTxt.setText(record.getPlace());
        dateTxt.setText(record.getDate());
        amountTxt.setText(String.format(Locale.US, "$%.2f", record.getAmount()));


        //Update color base on typeTxt
        typeTxt.setText(record.getTypeName());
        if(record.getTypeID() == 1){

            typeTxt.setTextColor(ContextCompat.getColor(this.context, R.color.bugetDepositTxt));
        }else{
            typeTxt.setTextColor(ContextCompat.getColor(this.context, R.color.budgetWithdrawTxt));
        }

        // Return the completed view to render on screen
        return view;
    }

}