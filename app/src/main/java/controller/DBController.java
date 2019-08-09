package controller;

import constant.Constant;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import model.BudgetModel;
import model.UserModel;
import model.PaymentModel;


public class DBController extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private Context context;
    private static DBController sInstance;
    private static Constant constant = new Constant();

    public static synchronized  DBController getInstance(Context context){
        if(sInstance == null){
            sInstance = new DBController(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBController(Context context){
        super(context, constant.DATABASE_NAME, null, constant.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User table
        this.createUserTable(db);

        // Create Budget table
        this.createBudgetTable(db);

        //Create Payment table
        this.createPaymentTable(db);
    }

    @Override
    public synchronized void close(){
        if (db != null)
            db.close();
        super.close();
    }

    public void openDB() throws SQLException{
        String path = "data/data" + this.context.getApplicationContext().getPackageName() + "/databases" + constant.DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    //----------------------        USER    ----------------------
    // table: User
    // field: full_name, balance

    public void addUser(String fullName, Double balance){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put("full_name", fullName);
            values.put("balance", balance);
            db.insert("User", null, values);
            db.setTransactionSuccessful();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<UserModel> getUser(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select u.full_name, u.balance from User as u";
        ArrayList<UserModel> user = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    user.add(new UserModel(cursor.getString(0), cursor.getDouble(1)));
                }while(cursor.moveToNext());
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return user;
    }

    public void updateUser(UserModel old_user, UserModel new_user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", new_user.getFullName());
        values.put("balance", new_user.getBalance());
        db.update("User", values, "full_name = '" + old_user.getFullName() + "'", null);
        db.close();
    }

    public void updateBalance(Double differentAmount){
        UserModel user = this.getUser().get(0);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", user.getBalance() + differentAmount);
        db.update("User", values, "full_name = '" + user.getFullName() + "'", null);
        db.close();
    }

    //----------------------        BUDGET    ----------------------
    // table: Budget
    // field: recordID, year, date, place, amount, typeID (1-deposit, 2- withdraw), typeName

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
                this.updateBalance(newRecord.getAmount());
            }else{
                this.updateBalance(-newRecord.getAmount());
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
                sql = "select * from Budget as b where strftime('%m', b.date) = '" + month + "' and b.year = '" + year + "' order by b.date DESC";
                break;
            case 1:
                sql = "select * from Budget as b where strftime('%m', b.date) = '" + month + "' and b.year = '" + year + "' and b.typeID = 1 order by b.date DESC";
                break;
            case 2:
                sql = "select * from Budget as b where strftime('%m', b.date) = '" + month + "' and b.year = '" + year + "' and b.typeID = 2 order by b.date DESC";
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
                this.updateBalance(-record.getAmount());
            } else {
                this.updateBalance(record.getAmount());
            }
        }
    }

    public void updateRecord(BudgetModel newRecord, BudgetModel oldRecord){
        //delete old
        this.deleteRecord(oldRecord);
        //Add new record
        this.addBudgetRecord(newRecord);
    }

    //----------------------        PAYMENT    ----------------------
    // table: Payment
    // field: paymentID, date, place, totalAmount, defaultAmount, monthlyStatus, currentMonth, completed

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

    public List<String> getAllPlaces(){
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
