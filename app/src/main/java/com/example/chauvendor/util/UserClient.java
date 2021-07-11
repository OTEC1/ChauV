package com.example.chauvendor.util;

import android.app.Application;
import android.content.Context;

import com.example.chauvendor.model.User;


public class UserClient extends Application {

    private User user = null;
    private  static   UserClient instances;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public  static  UserClient getInstance(){
        return instances;
    }
}
