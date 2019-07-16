package model;

public class PaymentModel {

    private int paymentID;
    private String date;
    private String place;
    private Double totalAmount;
    private Double defaultAmount;
    private int monthStatus;
    private int currentMonth;
    private int completed;

    public PaymentModel (int paymentID, String date, String place, Double totalAmount, Double defaultAmount, int monthStatus, int currentMonth, int complete){
        this.paymentID = paymentID;
        this.date = date;
        this.place = place;
        this.totalAmount = totalAmount;
        this.defaultAmount = defaultAmount;
        this.monthStatus = monthStatus;         // 0 - incomplete, 1 - complete
        this.currentMonth = currentMonth;
        this.completed = complete;              // 0 - incomplete, 1 - complete
    }

    public int getPaymentID() {
        return paymentID;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double newAmount){
        totalAmount = newAmount;
    }

    public Double getDefaultAmount() {
        return defaultAmount;
    }

    public int getMonthStatus() {
        return monthStatus;
    }

    public void setMonthStatus (int newMonthStatus){
        monthStatus = newMonthStatus;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int newCurrentMonth){
        currentMonth = newCurrentMonth;
    }

    public int getCompleted() {
        return completed;
    }


}
