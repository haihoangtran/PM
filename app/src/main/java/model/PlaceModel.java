package model;

public class PlaceModel {
    private int placeID;
    private String name;
    private String address;

    public PlaceModel (int placeID, String name, String address){
        this.placeID = placeID;
        this.name = name;
        this.address = address;
    }

    public int getPlaceID(){return placeID;}

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

}
