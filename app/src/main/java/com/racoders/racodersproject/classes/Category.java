package com.racoders.racodersproject.classes;

import com.racoders.racodersproject.R;

public class Category {
    private String name;
    private int image;

    public Category(){

    }

    public Category(String name, int image){
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public static int getImageFromName(String category) {
        if(category.equals("Bistro") || category.equals("Restaurant"))
            return R.drawable.categories_restaurant;
        else if (category.equals("Pub"))
            return R.drawable.categories_restaurant;
        else if (category.equals("Park"))
            return R.drawable.categories_park;
        else if (category.equals("Theatre"))
            return R.drawable.categories_theatre;
        else if (category.equals("Sport"))
            return R.drawable.categories_stadium;
        else if (category.equals("Parking"))
            return R.drawable.categories_parking;
        else if (category.equals("Festival"))
            return R.drawable.categories_festival;
        else if (category.equals("Hotel"))
            return R.drawable.categories_hotel;
        else if (category.equals("Cinema"))
            return R.drawable.categories_cinema;
        else
            return R.drawable.unknown;
    }

    public static int getBackgroundColor(String category){
        if(category.equals("Bistro") || category.equals("Restaurant"))
            return R.color.Bistro;
        else if (category.equals("Pub"))
            return R.color.Bistro;
        else if (category.equals("Park"))
            return R.color.Park;
        else if (category.equals("Theatre"))
            return R.color.Theatre;
        else if (category.equals("Sport"))
            return R.color.Stadium;
        else if (category.equals("Parking"))
            return R.color.lightGray;
        else if (category.equals("Festival"))
            return R.color.Festival;
        else if (category.equals("Hotel"))
            return R.color.Hotel;
        else if (category.equals("Cinema"))
            return R.color.Cinema;
        else
            return R.color.lightGray;

    }

}
