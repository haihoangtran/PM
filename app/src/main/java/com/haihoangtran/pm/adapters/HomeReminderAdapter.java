package com.haihoangtran.pm.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haihoangtran.pm.R;

import java.util.ArrayList;
import java.util.List;

import model.PaymentModel;

public class HomeReminderAdapter extends ArrayAdapter<PaymentModel> {

    private Context context;
    private List<PaymentModel> records;

    public HomeReminderAdapter(Context context, int resource, ArrayList<PaymentModel> records){
        super(context, resource, records);
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PaymentModel record = records.get(position);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.home_reminder_cell, null);

        // Define element in payment record cell
        TextView place = view.findViewById(R.id.place_txt);;
        ImageView image = view.findViewById(R.id.monthly_status_img);

        // Initialize value for each element
        place.setText(record.getPlace());
        if (record.getMonthStatus() == 1){
            image.setImageResource(R.drawable.ic_check_box);
        }else{
            image.setImageResource(R.color.transparentColor);
        }

        return view;
    }
}
