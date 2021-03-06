package com.example.chauvendor.Running_Service;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.chauvendor.R;
import com.example.chauvendor.constant.*;
import com.example.chauvendor.util.utils;

import java.net.URL;

import me.pushy.sdk.Pushy;

import static com.example.chauvendor.constant.Constants.*;

public class RegisterUser extends AsyncTask<Void,Void,Object> {

    private Activity activity;
    private SharedPreferences sp;
    private String TAG ="RegisterUser";

    public RegisterUser(Activity context) {
        this.activity = context;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            String deviceToken = Pushy.register(activity.getApplicationContext());
            new URL("https://com.example.chauvendor/regsiter/device?token="+deviceToken).openConnection();
            return  deviceToken;
        } catch (Exception e) {

            return e;
        }
    }


    @Override
    protected void onPostExecute(Object o) {

       if(o instanceof Exception){
           new utils().init(activity.getApplicationContext())
                   .edit().putBoolean(activity.getString(R.string.DEVICE_REG_TOKEN),false).apply();

           new utils().init(activity.getApplicationContext())
                   .edit().putString(activity.getString(R.string.DEVICE_TOKEN),"").apply();
       }
       else{
           new utils().init(activity.getApplicationContext())
                   .edit().putBoolean(activity.getString(R.string.DEVICE_REG_TOKEN),true).apply();

           new utils().init(activity.getApplicationContext())
                   .edit().putString(activity.getString(R.string.DEVICE_TOKEN),String.valueOf(o)).apply();
       }


    }
}
