package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import model.PaymentModel;

public class PaymentDB extends DBBase {
    // table: Payment
    // field: paymentID, date, place, totalAmount, defaultAmount, monthlyStatus, currentMonth, completed

    private static PaymentDB sInstance;

    public static synchronized  PaymentDB getInstance(Context context){
        if(sInstance == null){
            sInstance = new PaymentDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private PaymentDB(Context context){
        super(context);
    }

    public ArrayList<PaymentModel> getAllPaymentRecords(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from Payment";
        ArrayList<PaymentModel> pRecords = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    pRecords.add(new PaymentModel(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getDouble(3),
                            cursor.getDouble(4),
                            cursor.getInt(5),
                            cursor.getInt(6),
                            cursor.getInt(7)));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return pRecords;
    }

    public List<String> getAllPaymentPlaces(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select p.place from Payment as p";
        List<String> places = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    places.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return places;
    }

    public List<String> getPlacesOfIncompletePayment(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select p.place, p.completed from Payment as p";
        List<String> places = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(1) == 0)
                        places.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return places;
    }

    public ArrayList<PaymentModel> getPaymentRecodsByPlace(String place){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from Payment where place = '" + place + "'";
        ArrayList<PaymentModel> pRecords = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    pRecords.add(new PaymentModel(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getDouble(3),
                            cursor.getDouble(4),
                            cursor.getInt(5),
                            cursor.getInt(6),
                            cursor.getInt(7)));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return pRecords;
    }

    public void addPaymentRecord(PaymentModel newRecord){
        ArrayList<PaymentModel> existedRecords = getPaymentRecodsByPlace(newRecord.getPlace());
        if (existedRecords.size() > 0){
            newRecord.setTotalAmount(newRecord.getTotalAmount() + existedRecords.get(0).getTotalAmount());
            newRecord.setMonthStatus(existedRecords.get(0).getMonthStatus());
            newRecord.setCurrentMonth(existedRecords.get(0).getCurrentMonth());
            updatePaymentRecord(newRecord, existedRecords.get(0).getPaymentID());
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try{
                ContentValues values = new ContentValues();
                values.put("date", newRecord.getDate());
                values.put("place", newRecord.getPlace());
                values.put("totalAmount", newRecord.getTotalAmount());
                values.put("defaultAmount", newRecord.getDefaultAmount());
                values.put("monthlyStatus", newRecord.getMonthStatus());
                values.put("currentMonth", newRecord.getCurrentMonth());
                values.put("completed", newRecord.getCompleted());
                db.insert("Payment", null, values);
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
    }
    public void updatePaymentRecord(PaymentModel newRecord, int paymentID){
        //paymentID, date, place, totalAmount, defaultAmount, monthlyStatus, currentMonth, completed
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("paymentID", paymentID);
        values.put("date", newRecord.getDate());
        values.put("place", newRecord.getPlace());
        values.put("totalAmount", newRecord.getTotalAmount());
        values.put("defaultAmount", newRecord.getDefaultAmount());
        values.put("monthlyStatus", newRecord.getMonthStatus());
        values.put("currentMonth", newRecord.getCurrentMonth());
        values.put("completed", newRecord.getCompleted());
        db.update("Payment", values, "paymentID = " + paymentID , null);
        db.close();
    }

    public void updateMonthStatus(PaymentModel pRecord, int status, int currentMonth){
        // status: 0 - incomplete, 1 - complete
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("monthlyStatus", status);
        values.put("currentMonth", currentMonth);
        db.update("Payment", values, "paymentID = " + pRecord.getPaymentID() , null);
        db.close();

    }

    public void refreshPaymentCompleteStatus(){
        Enumeration<PaymentModel> pRecords = Collections.enumeration(this.getAllPaymentRecords());
        SQLiteDatabase db = this.getWritableDatabase();
        while(pRecords.hasMoreElements()){
            PaymentModel record = pRecords.nextElement();
            ContentValues values = new ContentValues();
            if(record.getTotalAmount() == 0.00){
                values.put("completed", 1);
            }else{
                values.put("completed", 0);
            }
            db.update("Payment", values, "paymentID = " + record.getPaymentID(), null);
        }
        db.close();
    }

    public void refreshPaymentMonthlyStatus(){
        Enumeration<PaymentModel> pRecords = Collections.enumeration(this.getAllPaymentRecords());
        int currentMonth = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH) + 1;
        while (pRecords.hasMoreElements()){
            PaymentModel record = pRecords.nextElement();
            if (record.getCurrentMonth() != currentMonth)
                this.updateMonthStatus(record, 0, currentMonth);
        }
    }

    public void deletePaymentRecord(PaymentModel record){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Payment", "paymentID = " + record.getPaymentID(), null);
        db.close();
    }

    /* ###################################################################
                            PRIVATE  FUNCTIONS
     ###################################################################*/

}
