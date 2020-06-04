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
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.haihoangtran.pm.R;
import model.BudgetModel;

public class BudgetRecordsAdapter extends ArrayAdapter<BudgetModel> {
    private Context context;
    private List<BudgetModel> records;

    public BudgetRecordsAdapter(Context context, int resource, ArrayList<BudgetModel> records){
        super(context, resource, records);
        this.context = context;
        this.records = records;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BudgetModel record = records.get(position);
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.budget_record_cell, null);
        }

        // Lookup view for data population
        TextView placeTxt = view.findViewById(R.id.place_txt);
        TextView dateTxt = view.findViewById(R.id.date_txt);
        ImageView emotionalFace = view.findViewById(R.id.emotion_face);
        TextView amountTxt = view.findViewById(R.id.amount_txt);

        // Populate the data into the template view using the data object
        placeTxt.setText(record.getPlace());
        dateTxt.setText(record.getDate());
        amountTxt.setText(String.format(Locale.US, "$%.2f", record.getAmount()));


        //Update color base on typeTxt
        if(record.getTypeID() == 1){
            emotionalFace.setImageResource(R.drawable.happy_face);
        }else{
            emotionalFace.setImageResource(R.drawable.sad_face);
        }

        // Return the completed view to render on screen
        return view;
    }

}