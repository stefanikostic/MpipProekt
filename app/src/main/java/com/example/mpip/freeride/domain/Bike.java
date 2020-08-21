package com.example.mpip.freeride.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;

public class Bike {
    private int id;
    private String name;
    private float price;
    private Bitmap imageUrl;
    private int rented;
    private Location location;
    private int renter;
    private int category;

    public Bike() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Bitmap getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Bitmap imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int isRented() {
        return rented;
    }

    public void setRented(int rented) {
        this.rented = rented;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRenter() {
        return renter;
    }

    public void setRenter(int renter) {
        this.renter = renter;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Bike(int id, String name, float price, Bitmap imageUrl, int rented, Location location, int renter, int category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rented = rented;
        this.location = location;
        this.renter = renter;
        this.category = category;
    }
}
