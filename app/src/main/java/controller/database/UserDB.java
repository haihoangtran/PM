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

    public ArrayList<UserModel> getAllSelectedUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList <UserModel> users = new ArrayList<>();
        String sql = "select * from User where selected == 1";
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    users.add(new UserModel(cursor.getInt(0),
                                            cursor.getString(1),
                                            cursor.getDouble(2),
                            cursor.getInt(3) == 1));
                }while(cursor.moveToNext());
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return users;
    }

    public void addUser(String fullName, Double balance, boolean selected){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put("full_name", fullName);
            values.put("balance", balance);
            values.put("selected", selected ? 1 : 0);
            db.insert("User", null, values);
            db.setTransactionSuccessful();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<UserModel> getAllUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select * from User";
        ArrayList<UserModel> users = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    users.add(new UserModel(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getDouble(2),
                            cursor.getInt(3) == 1));
                }while(cursor.moveToNext());
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return users;
    }

    public void updateUser(UserModel old_user, UserModel new_user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", new_user.getFullName());
        values.put("balance", new_user.getBalance());
        values.put("selected", new_user.getSelected() ? 1: 0);
        db.update("User", values, "userID = " + old_user.getUserID() , null);
        db.close();
    }

    public void updateBalance(Double differentAmount){
        UserModel user = this.getAllUsers().get(0);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", user.getBalance() + differentAmount);
        db.update("User", values, "userID = '" + user.getUserID() + "'", null);
        db.close();
    }

    public void updateSelectedUser(int selected_userID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            // Updated selected status = 1 for selected User ID
            values.put("selected", 1);
            db.update("User", values, "userID = " + selected_userID, null);

            // Updated selected status = 0 for not selected User ID
            values.clear();
            values.put("selected", 0);
            db.update("User", values, "userID != " + selected_userID, null);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteSelectedUser(int selected_userID){
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("User", "userID = " + selected_userID, null);
        db.close();
    }

}
