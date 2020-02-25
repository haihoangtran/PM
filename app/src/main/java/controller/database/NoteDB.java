package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
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

    public ArrayList<NoteModel> getAllNotes(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from Note order by noteID ASC";
        ArrayList<NoteModel> noteRecords = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    noteRecords.add(new NoteModel(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2)))   ;
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return noteRecords;
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
        // status: 0 - incomplete, 1 - complete
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newNote.getTitle());
        values.put("content", newNote.getContent());
        db.update("Note", values, "noteID = " + oldNoteID, null);
        db.close();
    }

    public void deleteNote(int noteID){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Note", "noteID = " + noteID, null);
        db.close();
    }
}
