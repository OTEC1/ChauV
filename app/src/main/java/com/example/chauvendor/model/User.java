package com.example.chauvendor.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String email, user_id, username,
             name,phone,member_T,app_user,
            img_url;


    private  int fair,good,bad;



    public User() {

    }

    protected User(Parcel in) {
        email = in.readString();
        user_id = in.readString();
        username = in.readString();
        img_url =in.readString();
        good =in.readInt();
        fair =in.readInt();
        bad =in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMember_T() {
        return member_T;
    }

    public void setMember_T(String member_T) {
        this.member_T = member_T;
    }

    public String getApp_user() {
        return app_user;
    }

    public void setApp_user(String app_user) {
        this.app_user = app_user;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getFair() {
        return fair;
    }

    public void setFair(int fair) {
        this.fair = fair;
    }

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", member_T='" + member_T + '\'' +
                ", app_user='" + app_user + '\'' +
                ", img_url='" + img_url + '\'' +
                ", good='" + good + '\'' +
                ", fair='" + fair + '\'' +
                ", bad='" + bad + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(app_user);
        dest.writeString(img_url);
        dest.writeInt(good);
        dest.writeInt(fair);
        dest.writeInt(bad);
    }
}

