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

public class PaymentRecordsAdapter extends ArrayAdapter<PaymentModel> {
    private Context context;
    private List<PaymentModel> records;

    public PaymentRecordsAdapter(Context context, int resource, ArrayList<PaymentModel> records){
        super(context, resource, records);
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PaymentModel record = records.get(position);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.payment_record_cell, null);

        // Define element in payment record cell
        TextView place = view.findViewById(R.id.pay_to_txt);
        TextView date = view.findViewById(R.id.start_date_txt);
        TextView defaultAmount = view.findViewById(R.id.default_amount_txt);
        TextView totalAmount = view.findViewById(R.id.total_amount_txt);
        ImageView image = view.findViewById(R.id.complete_status);

        // Initialize value for each element
        place.setText(record.getPlace());
        date.setText(record.getDate());
        defaultAmount.setText(String.format("%.2f", record.getDefaultAmount()));
        totalAmount.setText(String.format("%.2f", record.getTotalAmount()));
        if (record.getCompleted() == 1){
            image.setImageResource(R.drawable.ic_check_box);
        }else{
            image.setImageResource(R.color.transparentColor);
        }

        return view;
    }
}
