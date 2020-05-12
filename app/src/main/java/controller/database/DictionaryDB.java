package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
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
}
