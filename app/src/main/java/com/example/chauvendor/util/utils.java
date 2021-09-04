package com.example.chauvendor.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.example.chauvendor.UI.MainActivity;
import com.example.chauvendor.UI.Main_notification;
import com.example.chauvendor.UI.Vendor_account;
import com.example.chauvendor.UI.home;
import com.example.chauvendor.constant.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class utils {


    private boolean aBoolean;
    private SharedPreferences sp;



    public SharedPreferences start(Context context) {  return sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);}

    public SharedPreferences start_pref2(Context context) {
        return sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String Stringnify(Object s) {
        return String.valueOf(s);
    }

    public void message1(String s, Context x) {
        Toast.makeText(x, s, Toast.LENGTH_SHORT).show();
    }

    public void message(String s, AppCompatActivity a) {
        View parentLayout = a.findViewById(android.R.id.content);
        Snackbar.make(parentLayout, s, Snackbar.LENGTH_LONG).show();

    }


    public SharedPreferences instantiate_shared_preferences(SharedPreferences s, Context view) {
        return s = Objects.requireNonNull(view.getSharedPreferences(view.getString(R.string.app_name), Context.MODE_PRIVATE));
    }

    public static boolean doStringsMatch(String s1, String s2) {
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


    public void img_load(Context context, String url, ProgressBar progressBar_img, ImageView vendor_img) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);
        Glide.with(context)
                .load((String.valueOf(url)))
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


    public boolean bottom_nav(BottomNavigationView bottomNav, AppCompatActivity appCompatActivity, Bundle s) {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homes:
                        if (SIGN_IN_USER()) {
                            aBoolean = true;
                            appCompatActivity.startActivity(new Intent(appCompatActivity, MainActivity.class));
                            return true;
                        } else
                            request_user_sign_in(appCompatActivity);

                    case R.id.carts:
                        if (SIGN_IN_USER()) {
                            aBoolean = false;
                            appCompatActivity.startActivity(new Intent(appCompatActivity, Main_notification.class));
                            return true;
                        } else
                            request_user_sign_in(appCompatActivity);

                    case R.id.notification:
                        if (SIGN_IN_USER()) {
                            aBoolean = false;
                            appCompatActivity.startActivity(new Intent(appCompatActivity, Vendor_account.class));
                            return true;
                        } else
                            request_user_sign_in(appCompatActivity);

                }
                return false;
            }
        });
        return aBoolean;
    }


    public Map<String, Object> map(QueryDocumentSnapshot result, int r, String a) {
        Map<String, Object> pack = new HashMap<>();

        if (r == 1) {
            pack.put("Timestamp", TIME_FORMAT(result.get("TimeStamp").toString()));
            pack.put("doc_id", result.get("doc_id"));
            pack.put("food_name", result.get("food_name"));
            pack.put("food_price", result.get("food_price"));
            pack.put("img_url", result.get("img_url"));
            pack.put("item_id", result.get("item_id"));
            pack.put("quantity", result.get("quantity"));
            pack.put("cart_tracker", result.get("Cart_tracker"));
        } else if (r == 2) {
            pack.put("docs_id", result.getId());
            pack.put("Status", result.get("Status"));
            pack.put("TimeStamp", TIME_FORMAT(result.get("TimeStamp").toString()));
            pack.put("item_count", result.get("item_count"));
            pack.put("order_id", result.get("order_id"));
            pack.put("phone", result.get("phone"));
            pack.put("users", result.get("users"));
            pack.put("name", result.get("name"));
            pack.put("current_doc", a);
            pack.put("cart_tracker", result.get("Cart_tracker"));

        }
        return pack;
    }

    public String TIME_FORMAT(String result) {
        double l =  Double.parseDouble(result);
        long h = (long) l;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(h);
        return  cal.getTime().toString().replace("GMT+01:00"," ");
    }


    private void request_user_sign_in(AppCompatActivity a) {
        new utils().message2("Pls Sign in", a);
    }

    private boolean SIGN_IN_USER() {
        return FirebaseAuth.getInstance().getUid() != null;
    }


    //----------------------------------------------Fragment Change ---------------------------------------------//
    public void openFragment(Fragment fragment, AppCompatActivity appCompatActivity, Bundle s) {
        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragment.setArguments(s);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //Open Fragment from  Adapter Class
    public void open_Fragment(Fragment fragments, String tag, Context view, Bundle bundle, int d) {
        AppCompatActivity activity = (AppCompatActivity) view;
        Fragment myfrag = fragments;
        myfrag.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(d, myfrag, tag).addToBackStack(null).commit();

    }


    public void quick_commission_call(FirebaseFirestore firebaseFirestore, String TAG) {

        DocumentReference user = firebaseFirestore.collection("west").document("token");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Constants.CHARGES = String.valueOf(task.getResult().get("tokens"));

                } else
                    Log.d(TAG, String.valueOf(task.getException()));
            }
        });


    }


    public void api_call_to_cache(Context context, List<String> list, String tag, int os) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(context.getString(R.string.USER_REG));
        collectionReference.get().addOnCompleteListener(o -> {
            if (o.isSuccessful()) {
                for (QueryDocumentSnapshot s : o.getResult())
                    list.add(s.getId());
                CACHE_VENDOR_LIST(list, context, tag, os);


            }
        });


    }


    //read from cache list
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> multi_call_method(Context view, String tag) {
        String arrayListString = start_pref2(view).getString(tag, null);
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(arrayListString, type);

    }


    //Add to cache list
    public void CACHE_VENDOR_LIST(List<String> list, Context context, String tag, int N) {
        System.out.println(tag + "   " + list);
        if (N == 0)
            start_pref2(context).edit().putString(tag, null).apply();
        SharedPreferences.Editor collection = start_pref2(context).edit();
        String area = new Gson().toJson(list);
        collection.putString(tag, area);
        collection.apply();

    }







    //Cache Sign in User.
    public void CACHE_VENDOR(UserLocation first, Context context, int N, String tag) {
        if (N == 0)
            start(context).edit().putString(tag, null).apply();
        SharedPreferences.Editor collection =  start(context).edit();
        String area = new Gson().toJson(first);
        collection.putString(tag, area);
        collection.apply();

    }


    //Get Sign in user
    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserLocation GET_VENDOR_CACHED(Context view, String tag) {
        String arrayListString =  start(view).getString(tag, null);
        Type type = new TypeToken<UserLocation>() {}.getType();
        return new Gson().fromJson(arrayListString, type);

    }





    public  void sum_quantity(QueryDocumentSnapshot o,Context view,TextView total,long work_on,long send_in){
        if (total.getText().toString().trim().length() > 0)
            work_on = Integer.parseInt(clean(total.getText().toString(), view.getString(R.string.NAIRA)).trim());

        send_in = Integer.parseInt(String.valueOf(clean(Objects.requireNonNull(o.get("quantity"))
                .toString(), ".0"))) * Integer.parseInt(String.valueOf(new utils().clean(Objects.requireNonNull(o.get("food_price")).toString(), ".0")));
        total.setText(view.getString(R.string.NAIRA).concat(String.valueOf(work_on + send_in)));

    }

    private String clean(String toString, String string) {
        return toString.replace(string,"");
    }


//--------------------Remove a particular fragment  by tag--------------------------------//
//    public void clears(AppCompatActivity activity) {
//        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("gui");
//        if (fragment != null)
//            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//    }
}
