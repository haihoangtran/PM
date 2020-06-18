package com.haihoangtran.pm.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.haihoangtran.pm.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.DictionaryModel;

public class DictionaryWordRecordsAdapter extends ArrayAdapter<DictionaryModel>{
    private Context context;
    private List<DictionaryModel> wordRecords;
    private Button speakerBtn;
    private TextToSpeech myTTS = null;

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

        // Check TTS has voice data
        Intent ttsIntent = new Intent();
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        Activity currentActivity = (Activity) context;
        currentActivity.startActivityForResult(ttsIntent, 1000);

        // Define Text to speech variable
        myTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    if (myTTS != null) {
                        int result = myTTS.setLanguage(Locale.US);
                        if (result == TextToSpeech.LANG_MISSING_DATA ||
                                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS:", "lanugage is not supported");
                        } else {
                            Log.i("TTS:", "Initialize TTS successfully");
                        }
                    }
                } else {
                    Log.e("TTS:", "TTS initialization failed");
                }
            }
        });

        // Display word into text label
        TextView wordText = view.findViewById(R.id.word_text);
        wordText.setText(word.getWord());

        // Handle click on speaker button
        speakerBtn = view.findViewById(R.id.speaker_btn);
        this.speakerBtnHandle(word.getWord());
        return view;
    }

     /* ******************************************************
               PRIVATE FUNCTIONS
    *********************************************************/
     private void speakerBtnHandle(final String word){
         speakerBtn.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View view){
                 Toast.makeText(context, "Say: " + word, Toast.LENGTH_LONG).show();
                 myTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
             }
         });
     }


}
