package com.example.chauvendor.model;

public class Vendor_uploads {

  public   String food_name,img_url,category,uid,doc;
  public  int likes,views,food_price;



    public Vendor_uploads() {

    }


    public Vendor_uploads(int food_price, String food_name, String img_url, String category, String uid, int likes, int views, String doc) {
        this.food_price = food_price;
        this.food_name = food_name;
        this.img_url = img_url;
        this.category = category;
        this.uid = uid;
        this.likes = likes;
        this.views = views;
        this.doc = doc;
    }
}
