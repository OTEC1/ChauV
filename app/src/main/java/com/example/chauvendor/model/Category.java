package com.example.chauvendor.model;

import static com.example.chauvendor.util.Constants.IMG_URL;
import static com.example.chauvendor.constant.Constants.*;

public class Category {

    private String img_url,category,food_name;

    public Category() {
    }


    public String getCategory() {
        return category;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImg_url() { return IMG_URL+img_url; }

    public void setImg_url(String img_url) { this.img_url = img_url; }
}