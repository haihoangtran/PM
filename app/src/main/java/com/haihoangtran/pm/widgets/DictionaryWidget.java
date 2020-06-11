package com.haihoangtran.pm.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import controller.database.DictionaryDB;
import model.DictionaryModel;
import com.haihoangtran.pm.R;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class DictionaryWidget extends AppWidgetProvider {
    private DictionaryDB db;
    public ArrayList<DictionaryModel> wordModels;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String word) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dictionary_widget);
        views.setTextViewText(R.id.dictionary_word_txt, word);

        // Handle click on refresh button
        Intent intentRefresh = new Intent(context, DictionaryWidget.class);
        intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        PendingIntent pendingRefresh = PendingIntent.getBroadcast(context,appWidgetId, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.refresh_btn, pendingRefresh);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        db = DictionaryDB.getInstance(context);
        if(wordModels == null || wordModels.isEmpty()){
            wordModels = db.getAllWords();
        }
        // Get random element in wordModels, then remove it out of list
        // if wordModels is empty, return "N/A" as default
        String word = "N/A";
        if (!wordModels.isEmpty()){
            Random rand = new Random();
            int randomIndex = rand.nextInt(wordModels.size());
            word = wordModels.get(randomIndex).getWord();
            wordModels.remove(randomIndex);
        }
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, word);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

