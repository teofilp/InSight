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
            return R.drawable.f_restaurant;
        else if (category.equals("Pub"))
            return R.drawable.f_restaurant;
        else if (category.equals("Park"))
            return R.drawable.f_park;
        else if (category.equals("Theatre"))
            return R.drawable.f_theatre;
        else if (category.equals("Sport"))
            return R.drawable.f_stadium;
        else if (category.equals("Parking"))
            return R.drawable.f_parking;
        else if (category.equals("Festival"))
            return R.drawable.f_festival;
        else if (category.equals("Hotel"))
            return R.drawable.f_hotel;
        else if (category.equals("Cinema"))
            return R.drawable.f_cinema;
        else
            return R.drawable.you_marker;
    }

}
