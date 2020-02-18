package controller.database;

import android.content.Context;

public class NoteDB extends DBBase {
    // table: User
    // field: full_name, balance

    private static NoteDB sInstance;

    public static synchronized  NoteDB getInstance(Context context){
        if(sInstance == null){
            sInstance = new NoteDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private NoteDB(Context context){
        super(context);
    }


    public void addNoteTitle(String titleName){

    }

    public void editNoteTitle(int titleID, String newTitleName){

    }
}
