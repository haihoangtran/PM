package model;

import java.io.Serializable;

public class UserModel{
    private int userID;
    private String fullName;
    private Double balance;
    private boolean selected;

    public UserModel(int userID, String name, Double balance, boolean selected){
        this.userID = userID;
        this.fullName = name;
        this.balance = balance;
        this.selected = selected;
    }

    public int getUserID(){return this.userID;}

    public String getFullName(){
        return this.fullName;
    }

    public Double getBalance(){
        return this.balance;
    }

    public boolean getSelected(){ return this.selected;}

}
