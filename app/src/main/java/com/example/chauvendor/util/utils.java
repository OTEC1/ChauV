package com.example.chauvendor.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.chauvendor.R;
import com.example.chauvendor.UI.account;
import com.example.chauvendor.UI.home;
import com.example.chauvendor.UI.notification;
import com.example.chauvendor.constant.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class utils {


    private boolean aBoolean;


    public String  Stringnify(Object s){
        return String.valueOf(s);
    }

    public   void message1(String s, Context x){
        Toast.makeText(x, s, Toast.LENGTH_SHORT).show();
    }

    public void message(String s, AppCompatActivity a) {
        View parentLayout = a.findViewById(android.R.id.content);
        Snackbar.make(parentLayout, s, Snackbar.LENGTH_LONG).show();

    }



    public SharedPreferences instantiate_shared_preferences(SharedPreferences s, Context view) {
        return s = Objects.requireNonNull(view.getSharedPreferences(view.getString(R.string.app_name), Context.MODE_PRIVATE));
    }

    public static boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }

    public void check_edit_text(EditText edit, String string) {
        if (edit.getText().toString().isEmpty()) {
            edit.setError(string);
            edit.requestFocus();
        }
    }


    public void message2(String s, Activity a) {
        View parentLayout = a.findViewById(android.R.id.content);
        Snackbar.make(parentLayout, s, Snackbar.LENGTH_SHORT).show();

    }


    public void img_load(Context context, String task, ProgressBar progressBar_img, ImageView vendor_img) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        Glide.with(context)
                .load(Constants.IMG_URL.concat(String.valueOf(task)))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar_img.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar_img.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(requestOptions)
                .into(vendor_img);

    }





    public boolean bottom_nav(BottomNavigationView bottomNav,AppCompatActivity appCompatActivity, Bundle s) {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homes:
                        if (SIGN_IN_USER()) {
                            aBoolean=true;
                            openFragment(new home(),appCompatActivity,s);
                            return true;
                        } else
                            request_user_sign_in(appCompatActivity);

                    case R.id.carts:
                        if (SIGN_IN_USER()) {
                            aBoolean = false;
                            openFragment(new notification(),appCompatActivity,s);
                            return true;
                        } else
                            request_user_sign_in(appCompatActivity);
                    case R.id.notification:
                        if (SIGN_IN_USER()) {
                            aBoolean = false;
                            openFragment(new account(),appCompatActivity,s);
                            return true;
                        } else
                            request_user_sign_in(appCompatActivity);

                }
                return false;
            }
        });
        return  aBoolean;
    }





    private void request_user_sign_in(AppCompatActivity a) {
        new utils().message2("Pls Sign in", a);
    }

    private boolean SIGN_IN_USER() {
        return FirebaseAuth.getInstance().getUid() != null;
    }




    //----------------------------------------------Fragment Change ---------------------------------------------//
  public   void openFragment(Fragment fragment, AppCompatActivity appCompatActivity, Bundle s) {
        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragment.setArguments(s);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }




//--------------------Remove a particular fragment  by tag--------------------------------//
//    public void clears(AppCompatActivity activity) {
//        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("gui");
//        if (fragment != null)
//            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//    }
}
