package com.example.mobiledevlabs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Country {
    @SerializedName("square")
    @Expose
    private String Square;

    @SerializedName("Capital")
    @Expose
    private String Capital;

    @SerializedName("Name")
    @Expose
    private String Name;

    @SerializedName("Flag")
    @Expose
    private int Flag;

    Country (String name, String capital, String square) {
        setName(name);
        setCapital(capital);
        setSquare(square);
    }

    String getSquare() {
        return Square;
    }

    void setSquare(String square) {
        Square = square;
    }

    String getCapital() {
        return Capital;
    }

    void setCapital(String capital) {
        Capital = capital;
    }

    String getName() {
        return Name;
    }

    void setName(String name) {
        Name = name;
    }

    int getFlag() {
        return Flag;
    }

    void setFlag(int flag) {
        Flag = flag;
    }
}


