package com.example.chauvendor.model;

import static com.example.chauvendor.util.Constants.IMG_URL;
import static com.example.chauvendor.constant.Constants.*;

public class Category {

    private String img_url,category,food_name,doc;

    public Category() {
    }


    public String getCategory() {
        return category;
    }

    public String getFood_name() {
        return food_name;
    }

    public String getImg_url() { return IMG_URL+img_url; }

    public String getDoc() {
        return doc;
    }

}