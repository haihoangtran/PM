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
import model.DictionaryModel;

public class DictionaryWordRecordsAdapter extends ArrayAdapter<DictionaryModel> {
    private Context context;
    private List<DictionaryModel> wordRecords;

    public DictionaryWordRecordsAdapter(Context context, int resource, ArrayList<DictionaryModel> wordRecords){
        super(context, resource, wordRecords);
        this.context = context;
        this.wordRecords = wordRecords;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        DictionaryModel word = wordRecords.get(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.words_record_cell, null);
        }

        TextView wordText = view.findViewById(R.id.word_text);
        wordText.setText(word.getWord());
        return view;
    }
}
