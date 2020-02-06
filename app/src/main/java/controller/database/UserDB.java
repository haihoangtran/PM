package controller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import model.UserModel;

public class UserDB extends DBBase {
    // table: User
    // field: full_name, balance

    private static UserDB sInstance;

    public static synchronized  UserDB getInstance(Context context){
        if(sInstance == null){
            sInstance = new UserDB(context.getApplicationContext());
        }
        return sInstance;
    }

    private UserDB(Context context){
        super(context);
    }

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

    /* ###################################################################
                            PRIVATE  FUNCTIONS
     ###################################################################*/


}
