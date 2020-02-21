package controller.database;

import constant.Constant;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;


public class DBBase extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private Context context;
    private static DBBase sInstance;
    private static Constant constant = new Constant();

    public static synchronized DBBase getInstance(Context context){
        if(sInstance == null){
            sInstance = new DBBase(context.getApplicationContext());
        }
        return sInstance;
    }

    public DBBase(Context context){
        super(context, constant.DATABASE_NAME, null, constant.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        createAllTables(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
    }

    @Override
    public synchronized void close(){
        if (db != null)
            db.close();
        super.close();
    }


    /* ###################################################################
                            PRIVATE  FUNCTIONS
     ###################################################################*/
    private void createAllTables(SQLiteDatabase db){
        createUserTable(db);
        createBudgetTable(db);
        createPaymentTable(db);
        createPlaceTable(db);
        createNoteTable(db);
    }

    private void createUserTable(SQLiteDatabase db){
        /*
        field: full name, balance
         */
        String sql = "Create table if not exists User (full_name text, balance real, primary key(full_name))";
        db.execSQL(sql);
    }

    private void createBudgetTable(SQLiteDatabase db){
        /*
        field: recordID, year, date, place, amount, typeID (1-deposit, 2- withdraw), typeName
         */
        String sql = "Create table if not exists Budget (recordID integer primary key AUTOINCREMENT, " +
                "year text not null, date text not null, place text, amount real, typeID integer, typeName text )";
        db.execSQL(sql);
    }

    private void createPaymentTable(SQLiteDatabase db){
        /*
        field: paymentID, date, place, totalAmount, defaultAmount, monthlyStatus, currentMonth, completed
         */
        String sql = "create table if not exists Payment (paymentID integer primary key AUTOINCREMENT, " +
                "date text, place text not null, totalAmount real, defaultAmount real, " +
                "monthlyStatus integer, currentMonth integer, completed integer)";
        db.execSQL(sql);
    }

    private void createPlaceTable(SQLiteDatabase db){
        /*
        field: placeID, placeName, placeAddr
         */
        String sql = "Create table if not exists Place (placeID integer primary key AUTOINCREMENT, " +
                "placeName text not null, placeAddr text not null)";
        db.execSQL(sql);
    }

    private void createNoteTable(SQLiteDatabase db){
        /*
        field: noteID, title, content
         */
        String sql = "Create table if not exists Note (noteID integer primary key AUTOINCREMENT, " +
                "title text not null, content text not null)";
        db.execSQL(sql);
    }
}
