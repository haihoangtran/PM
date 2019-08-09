package model;


import java.io.Serializable;

public class BudgetModel implements Serializable{
    private int recordID;
    private String year;
    private String date;
    private String place;
    private Double amount;
    private int typeID;
    private String typeName;

    public BudgetModel(int recordID, String year, String date, String place, Double amount, int typeID, String typeName){
        this.recordID = recordID;
        this.year = year;
        this.date = date;
        this.place = place;
        this.amount = amount;
        this.typeID = typeID;   // 1= deposit , 2 = withdraw
        this.typeName = typeName;
    }

    public int getRecordID() {
        return recordID;
    }
    public String getYear(){return year;}

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public Double getAmount() {
        return amount;
    }

    public int getTypeID() {
        return typeID;
    }
    public String getTypeName(){
        return typeName;
    }
}