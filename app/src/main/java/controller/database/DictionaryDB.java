package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import model.DictionaryModel;

public class DictionaryDB extends DBBase{
    /*
    table: Dictionary
    field: word
     */

    private static DictionaryDB sInstance;

    public static synchronized  DictionaryDB getInstance(Context context){
        if (sInstance == null){
            sInstance = new DictionaryDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private DictionaryDB (Context context){super(context);}

    public boolean addWord(DictionaryModel newWord){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("word", newWord.getWord());
            db.insertOrThrow("Dictionary", null, values);
            db.setTransactionSuccessful();
            return true;
        }catch(SQLiteConstraintException e){
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db.isOpen()){
                db.endTransaction();
                db.close();
            }
        }
    }

    public ArrayList<DictionaryModel> getAllWords(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from Dictionary order by word ASC";
        ArrayList<DictionaryModel> wordRecords = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    wordRecords.add(new DictionaryModel(cursor.getString(0))) ;
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return wordRecords;
    }

    public ArrayList<DictionaryModel> getWordsByKeyWord(String keyword){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from Dictionary where word like '%" + keyword.toLowerCase() + "%' order by word ASC";
        ArrayList<DictionaryModel> wordRecords = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    wordRecords.add(new DictionaryModel(cursor.getString(0))) ;
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return wordRecords;
    }

    public boolean updateWord(DictionaryModel newWord, DictionaryModel oldWord){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", newWord.getWord());
        try {
            db.update("Dictionary", values, "word = '" + oldWord.getWord() + "'", null);
            return true;
        }catch(SQLiteConstraintException e){
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db.isOpen()){
                db.close();
            }
        }
    }

    public void deleteWord(String word){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Dictionary", "word = '" + word + "'", null);
        db.close();
    }
}
