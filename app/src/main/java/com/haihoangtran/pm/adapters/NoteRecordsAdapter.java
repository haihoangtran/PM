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
import model.NoteModel;

public class NoteRecordsAdapter extends ArrayAdapter<NoteModel> {
    private Context context;
    private List<NoteModel> noteRecords;

    public NoteRecordsAdapter(Context context, int resource, ArrayList<NoteModel> noteRecords){
        super(context, resource, noteRecords);
        this.context = context;
        this.noteRecords = noteRecords;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        // Get specific note by position
        NoteModel note = noteRecords.get(position);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.note_title_record_cell, null);

        TextView title = view.findViewById(R.id.note_title_text);
        title.setText(note.getTitle());
        return view;
    }
}
