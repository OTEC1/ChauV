package com.example.chauvendor.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Food_name_sub  implements Parcelable {

    private String  food_name,img_url,category,uid,doc;
    private int food_price,likes,dislikes,views;
    private Map<String, Object> z_map = new HashMap<>();



    public  Food_name_sub(){

    }


    protected Food_name_sub(Parcel in) {
        food_name  = in.readString();
        img_url = in.readString();
        category = in.readString();
        uid =  in.readString();
        doc = in.readString();
        food_price = in.readInt();
        likes = in.readInt();
        dislikes  = in.readInt();
        views = in.readInt();
        in.readMap(z_map,Map.class.getClassLoader());


    }


    public static Creator<Food_name_sub> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<Food_name_sub> CREATOR = new Creator<Food_name_sub>() {
        @Override
        public Food_name_sub createFromParcel(Parcel in) {
            return new Food_name_sub(in);
        }

        @Override
        public Food_name_sub[] newArray(int size) {
            return new Food_name_sub[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }





    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(food_name);
        dest.writeString(img_url);
        dest.writeString(category);
        dest.writeString(uid);
        dest.writeString(doc);
        dest.writeInt(food_price);
        dest.writeInt(likes);
        dest.writeInt(dislikes);
        dest.writeInt(views);
        dest.writeMap(z_map);
    }

    public Map<String, Object> getZ_map() {
        return z_map;
    }

    public void setZ_map(Map<String, Object> z_map) {
        this.z_map = z_map;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getFood_price() {
        return food_price;
    }

    public void setFood_price(int food_price) {
        this.food_price = food_price;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }


    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

}
