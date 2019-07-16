package model;

import java.io.Serializable;

public class UserModel{
    private String fullName;
    private Double balance;

    public UserModel(String name, Double balance){
        this.fullName = name;
        this.balance = balance;
    }

    public String getFullName(){
        return this.fullName;
    }

    public Double getBalance(){
        return this.balance;
    }

}
