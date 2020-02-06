package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import model.PlaceModel;

public class PlaceDB extends DBBase {

    // table: Place
    // field: placeID, placeName, placeAddr

    private static PlaceDB sInstance;

    public static synchronized  PlaceDB getInstance(Context context){
        if(sInstance == null){
            sInstance = new PlaceDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private PlaceDB(Context context){
        super(context);
    }

    public void addPlace(PlaceModel newPlace){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put("placeName", newPlace.getName());
            values.put("placeAddr", newPlace.getAddress());
            db.insert("Place", null, values);
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

    public ArrayList<PlaceModel> getAllPlaces(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from Place order by placeName ASC";
        ArrayList<PlaceModel> placeRecords = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    placeRecords.add(new PlaceModel(cursor.getInt(0),
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
        return placeRecords;
    }

    public void updatePlace(PlaceModel place, int placeID){
        // status: 0 - incomplete, 1 - complete
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("placeName", place.getName());
        values.put("placeAddr", place.getAddress());
        db.update("Place", values, "placeID = " + placeID, null);
        db.close();
    }

    public void deletePlace(int placeID){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Place", "placeID = " + placeID, null);
        db.close();
    }

    /* ###################################################################
                            PRIVATE  FUNCTIONS
     ###################################################################*/
}
