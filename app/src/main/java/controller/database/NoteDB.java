package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import model.NoteModel;

public class NoteDB extends DBBase {
    // table: Note
    // field: noteID, title, content

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


    public void addNote(NoteModel note){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put("title", note.getTitle());
            values.put("content", note.getContent());
            db.insert("Note", null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (db.isOpen()){
                db.endTransaction();
                db.close();
            }
        }
    }

    public void updateNote(NoteModel newNote, int oldNoteID){

    }
}
