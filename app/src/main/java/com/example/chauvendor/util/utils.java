package com.example.chauvendor.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import androidx.fragment.app.FragmentActivity;
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
import com.example.chauvendor.constant.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;


public class utils {


    private boolean aBoolean;
    private SharedPreferences sp;


    public String Stringify(Object x) {
        return String.valueOf(x);
    }


    public SharedPreferences init(Context context) {
        return sp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

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
        Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
    }


    public void message2(String s, Activity a) {
        View parentLayout = a.findViewById(android.R.id.content);
        Snackbar snack = Snackbar.make(parentLayout, s, Snackbar.LENGTH_LONG);
        snack.show();

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


    public void img_load(Context context, String url, ProgressBar progressBar_img, ImageView vendor_img) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(context).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable  GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
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






    public boolean bottom_nav(BottomNavigationView bottomNav, AppCompatActivity appCompatActivity, ProgressBar progressBar) {

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homes:
                    progressBar.setVisibility(View.VISIBLE);
                    if (SIGN_IN_USER()) {
                        aBoolean = true;
                        appCompatActivity.startActivity(new Intent(appCompatActivity, MainActivity.class));
                        return true;
                    } else
                        request_user_sign_in(appCompatActivity);

                case R.id.carts:
                    progressBar.setVisibility(View.VISIBLE);
                    if (SIGN_IN_USER()) {
                        aBoolean = false;
                        appCompatActivity.startActivity(new Intent(appCompatActivity, Main_notification.class));
                        return true;
                    } else
                        request_user_sign_in(appCompatActivity);

                case R.id.notification:
                    progressBar.setVisibility(View.VISIBLE);
                    if (SIGN_IN_USER()) {
                        aBoolean = false;
                        appCompatActivity.startActivity(new Intent(appCompatActivity, Vendor_account.class));
                        return true;
                    } else
                        request_user_sign_in(appCompatActivity);

            }
            return false;
        });
        return aBoolean;
    }


    public Map<String, Object> map(QueryDocumentSnapshot result) {
        Map<String, Object> pack = new TreeMap<>();
        pack.put("Timestamp", TIME_FORMAT(result.get("TimeStamp").toString()));
        pack.put("doc_id", result.get("doc_id"));
        pack.put("food_name", result.get("food_name"));
        pack.put("food_price", result.get("food_price"));
        pack.put("img_url", result.get("img_url"));
        pack.put("item_id", result.get("item_id"));
        pack.put("quantity", result.get("quantity"));
        pack.put("cart_tracker", result.get("Cart_tracker"));
        pack.put("star_boi", result.get("star_boi"));
        pack.put("food", result.get("food"));
        return pack;
    }


    public Map<String, Object> map_data(QueryDocumentSnapshot result, String a) {
        Map<String, Object> pack = new TreeMap<>();
        pack.put("docs_id", result.getId());
        pack.put("dstatus", result.get("DStatus"));
        pack.put("TimeStamp", TIME_FORMAT(result.get("TimeStamp").toString()));
        pack.put("vstatus", result.get("VStatus"));
        pack.put("item_count", result.get("item_count"));
        pack.put("name", result.get("name"));
        pack.put("order_id", result.get("order_id"));
        pack.put("phone", result.get("phone"));
        pack.put("users", result.get("users"));
        pack.put("current_doc", a);
        pack.put("cart_tracker", result.get("Cart_tracker"));
        pack.put("OBJ", result.get("OBJ"));

        return pack;
    }


    public String TIME_FORMAT(String result) {
        double l = Double.parseDouble(result);
        long h = (long) l;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(h);
        cal.setTimeZone(Calendar.getInstance().getTimeZone());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:s");
        Date x = cal.getTime();
        return formatter.format(x);

    }


    public Map<String, Object> mapping(Map<String, Object> result) {
        Map<String, Object> pack = new TreeMap<>();

        pack.put("docs_id", result.get("docs_id"));
        pack.put("dstatus", result.get("dstatus"));
        pack.put("TimeStamp", result.get("TimeStamp"));
        pack.put("vstatus", result.get("vstatus"));
        pack.put("item_count", result.get("item_count"));
        pack.put("name", result.get("name"));
        pack.put("order_id", result.get("order_id"));
        pack.put("phone", result.get("phone"));
        pack.put("users", result.get("users"));
        pack.put("current_doc", result.get("current_doc"));
        pack.put("cart_tracker", result.get("cart_tracker"));
        pack.put("OBJ", result.get("OBJ"));


        return pack;
    }


    private void request_user_sign_in(AppCompatActivity a) {
        new utils().message2("Pls Sign in", a);
    }


    private boolean SIGN_IN_USER() {
        return FirebaseAuth.getInstance().getUid() != null;
    }


    //Open Fragment from  Activity Class
    public void openFragment(Fragment fragment, AppCompatActivity appCompatActivity, Bundle s) {
        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragment.setArguments(s);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //open from  fragment
    public void openFragments(Fragment fragment, FragmentActivity appCompatActivity, Bundle s) {
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


    public void quick_commission_call(String TAG) {
        FirebaseFirestore.getInstance().collection("admins").document("Teasers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Constants.CHARGES = String.valueOf(task.getResult().get("tokens"));
                Constants.REQUEST_KEY = task.getResult().get("Push_4_chauD").toString();
            } else
                Log.d(TAG, String.valueOf(task.getException()));
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


    //Cache Sign in Vendor.
    public void CACHE_VENDOR(UserLocation first, Context context, int N, String tag) {
        if (N == 0)
            init(context).edit().putString(tag, null).apply();
        SharedPreferences.Editor collection = init(context).edit();
        String area = new Gson().toJson(first);
        collection.putString(tag, area);
        collection.apply();

    }


    //Get Sign in Vendor.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserLocation GET_VENDOR_CACHED(Context view, String tag) {
        String arrayListString = init(view).getString(tag, null);
        Type type = new TypeToken<UserLocation>() {}.getType();
        return new Gson().fromJson(arrayListString, type);

    }


    public void VENDOR_LOCATION_QUERY(ProgressBar progressBar, AppCompatActivity context, EditText editText) {

        FirebaseFirestore.getInstance().collection(context.getString(R.string.Vendor_loc)).document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(h -> {
            if (h.isSuccessful()) {
                UserLocation users = new UserLocation();
                users.setUser(h.getResult().get("user", User.class));
                users.setGeo_point(h.getResult().getGeoPoint("geo_point"));
                CACHE_VENDOR(users, context, 0, context.getString(R.string.VENDOR));
                context.startActivity(new Intent(context, MainActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            } else
                new utils().message2("Error Getting " + editText.getText().toString() + " Vendor Details ! ", context);

        });

    }


    public void sum_quantity(QueryDocumentSnapshot o, Context view, TextView total, long work_on, long send_in) {
        if (total.getText().toString().trim().length() > 0)
            work_on = Integer.parseInt(clean(total.getText().toString(), view.getString(R.string.NAIRA)).trim());

        send_in = Integer.parseInt(String.valueOf(clean(Objects.requireNonNull(o.get("quantity"))
                .toString(), ".0"))) * Integer.parseInt(String.valueOf(new utils().clean(Objects.requireNonNull(o.get("food_price")).toString(), ".0")));
        total.setText(view.getString(R.string.NAIRA).concat(String.valueOf(work_on + send_in)));

    }


    private String clean(String toString, String string) {
        return toString.replace(string, "");
    }

    public Map<String, Object> maps() {
        Map<String, Object> obj = new HashMap<>();
        obj.put("VStatus", true);
        return obj;

    }


    public void buildAlertMessageNoGps(Context context, int a, String as) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setMessage(as)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, id) -> {
                    if (a == 0) {
                        Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent1);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public boolean verify(EditText editText1, EditText editText2, EditText editText3, EditText editText4, EditText editText5, EditText editText6, String string, AppCompatActivity context) {
        if (editText1.getText().toString().isEmpty()) {
            new utils().check_edit_text(editText1, "Pls fill out field");
            return false;
        } else if (editText2.getText().toString().isEmpty()) {
            new utils().check_edit_text(editText2, "Pls fill out field");
            return false;
        } else if (editText3.getText().toString().isEmpty()) {
            new utils().check_edit_text(editText3, "Pls fill out field");
            return false;
        } else if (editText4.getText().toString().isEmpty()) {
            new utils().check_edit_text(editText4, "Pls fill out field");
            return false;
        } else if (editText5.getText().toString().isEmpty()) {
            new utils().check_edit_text(editText5, "Pls fill out field");
            return false;

        } else if (editText6.getText().toString().isEmpty()) {
            new utils().check_edit_text(editText6, "Pls fill out field");
            return false;

        } else if (!doStringsMatch(editText4.getText().toString(), editText5.getText().toString())) {
            new utils().message2("Password does not match", context);
            return false;
        } else if (string.equals("Vendor Category")) {
            new utils().message2("Pls Indicate vendor type.", context);
            return false;
        } else
            return true;
    }


    public Map<String, Object> MAP(String names, String account, String bank_selected) {
        long x = Long.parseLong(account);
        Map<String, Object> carry = new HashMap<>();
        carry.put("account_name", names);
        carry.put("Bank", bank_selected);
        carry.put("account", x);
        carry.put("ID", FirebaseAuth.getInstance().getUid());
        return carry;
    }
}


//--------------------Remove a particular fragment  by tag--------------------------------//
//    public void clears(AppCompatActivity activity) {
//        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("gui");
//        if (fragment != null)
//            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//    }


// View view =getSupportActionBar().getCustomView();


