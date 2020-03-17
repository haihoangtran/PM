package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.BudgetModel;

public class BudgetDB extends DBBase {
    // table: Budget
    // field: recordID, year, date, place, amount, typeID (1-deposit, 2- withdraw), typeName
    private static BudgetDB sInstance;
    private UserDB userDB;

    public static synchronized  BudgetDB getInstance(Context context){
        if(sInstance == null){
            sInstance = new BudgetDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private BudgetDB(Context context){
        super(context);
        userDB = UserDB.getInstance(context);
    }

    public void addBudgetRecord(BudgetModel newRecord){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put("year", newRecord.getYear());
            values.put("date", this.convertDatetoSQLDate(newRecord.getDate()));
            values.put("place", newRecord.getPlace());
            values.put("amount", newRecord.getAmount());
            values.put("typeID", newRecord.getTypeID());
            values.put("typeName", newRecord.getTypeName());
            db.insert("Budget", null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            // Update balance of user typeID = 1 => balance + amount, typeId = 2 => balance - amount
            if (newRecord.getTypeID() == 1){
                userDB.updateBalance(newRecord.getAmount());
            }else{
                userDB.updateBalance(-newRecord.getAmount());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (db.isOpen()){
                db.endTransaction();
                db.close();
            }
        }
    }

    public ArrayList<BudgetModel> getMonthlyRecords(String month, String year, int displayType){
        /*
        displayType: 0 - all, 1 - deposit, 2 - withdraw
         */
        String sql = "";
        switch (displayType){
            case 0:
                sql = "select * from Budget as b where strftime('%m', b.date) = '" + month + "' and b.year = '" + year + "' order by b.recordID DESC";
                break;
            case 1:
                sql = "select * from Budget as b where strftime('%m', b.date) = '" + month + "' and b.year = '" + year + "' and b.typeID = 1 order by b.recordID DESC";
                break;
            case 2:
                sql = "select * from Budget as b where strftime('%m', b.date) = '" + month + "' and b.year = '" + year + "' and b.typeID = 2 order by b.recordID DESC";
                break;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<BudgetModel> records = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    records.add(new BudgetModel(cursor.getInt(0), cursor.getString(1),
                            convertSQLDatetoDate(cursor.getString(2)),
                            cursor.getString(3),
                            cursor.getDouble(4),
                            cursor.getInt(5),
                            cursor.getString(6)));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return records;
    }

    public List<String> getYears(){
        /*
        Get list of years in Monthly_Total table
         */
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select year from Budget group by year";
        List<String> years = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    years.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return years;
    }

    public Double getMonthlyTotal(String month, String year, int typeID){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select sum(b.amount) from Budget as b where strftime('%m', b.date) = '" + month + "' and b.year = '" + year + "' and b.typeID = " + typeID;
        Double total = 0.00;
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    total = cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return total;
    }

    public void deleteRecord(BudgetModel record){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Budget", "recordID = " + record.getRecordID(), null);
        db.close();
        if(rows == 1) {
            // Update balance of user typeID = 1 => balance - amount, typeId = 2 => balance + amount
            if (record.getTypeID() == 1) {
                userDB.updateBalance(-record.getAmount());
            } else {
                userDB.updateBalance(record.getAmount());
            }
        }
    }

    public void deleteRecordsByYear(String year){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Budget", "year = " + year, null);
        db.close();
    }

    public void updateRecord(BudgetModel newRecord, BudgetModel oldRecord){
        //delete old
        this.deleteRecord(oldRecord);
        //Add new record
        this.addBudgetRecord(newRecord);
    }

    /* ###################################################################
                            PRIVATE  FUNCTIONS
     ###################################################################*/

    private String convertDatetoSQLDate(String date){
        /*
        Convert date (MM/dd/YYYY) to (YYYY-MM-DD) for sqlite
         */
        String destDate="";
        try {
            Date srcDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
            destDate = new SimpleDateFormat("yyyy-MM-dd").format(srcDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return destDate;
    }

    private String convertSQLDatetoDate(String sqlDate){
        /*
        Convert a sql date (YYYY-MM-DD) to (MM/dd/YYYY) for sqlite
         */
        String destDate="";
        try {
            Date srcDate = new SimpleDateFormat("yyyy-MM-dd").parse(sqlDate);
            destDate = new SimpleDateFormat("MM/dd/yyyy").format(srcDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return destDate;
    }
}
