package com.haihoangtran.pm.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.haihoangtran.pm.R;
import java.util.ArrayList;
import java.util.List;
import model.PlaceModel;

public class PlaceRecordsAdapter extends ArrayAdapter<PlaceModel> {
    private Context context;
    private List<PlaceModel> placeRecords;

    public PlaceRecordsAdapter(Context context, int resource, ArrayList<PlaceModel> placeRecords){
        super(context, resource, placeRecords);
        this.context = context;
        this.placeRecords = placeRecords;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PlaceModel place = placeRecords.get(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.place_record_cell, null);
        }

        // Lookup view for data population
        TextView name = view.findViewById(R.id.place_name_text);
        TextView address = view.findViewById(R.id.place_address_text);

        // Populate the data into the template view using the data object
        name.setText(place.getName());
        address.setText(place.getAddress());

        // Return the completed view to render on screen
        return view;
    }
}
