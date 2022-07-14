package com.example.mystoryapp;

// Custom class used to store entry data from Firebase
public class EntryModel {

    private String date;
    private String location;
    private String entry;
    //private String image;

    // Getter and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /* public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    } */

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    // Constructors
    EntryModel(String date, String entry, String location){
        this.date = date;
        this.location = location;
        this.entry = entry;
    }

    EntryModel(){ }

    // USED FOR TESTING
    @Override
    public String toString() {
        return "Date: " + date + " Entry: "+ entry + " Location: " + location;
    }
}
