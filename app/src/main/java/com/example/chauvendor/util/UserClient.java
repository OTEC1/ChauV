package com.example.chauvendor.util;

import android.app.Application;
import android.content.Context;


public class UserClient extends Application {

    private User user = null;
    private  static   UserClient instances;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
