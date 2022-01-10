package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.example.chauvendor.R;
import com.example.chauvendor.Running_Service.Keep_alive;
import com.example.chauvendor.Running_Service.RegisterUser;
import com.example.chauvendor.util.UserLocation;
import com.example.chauvendor.util.utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.pushy.sdk.Pushy;

import static com.example.chauvendor.constant.Constants.*;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Bundle bundle = new Bundle();
    private ProgressBar ui_loader;


    private String TAG = "MainActivity";
    private static long back_pressed;
    private static int Time_lapsed = 2000;
    private boolean decide = false;



    @Override
    protected void onResume() {
        super.onResume();
        decide = true;
        if (FirebaseAuth.getInstance().getUid() != null && CHECKED()) {
            if (CHARGES == null)
                new utils().quick_commission_call(TAG);
            new utils().api_call_to_cache(getApplicationContext(), new ArrayList<>(), getString(R.string.CACHE_LIST_OF_VENDORS), 1);
            new utils().openFragment(new home(), this, bundle);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottomNav);
        ui_loader = (ProgressBar) findViewById(R.id.ui_loader);

        NOTIFICATION_LISTER(new Keep_alive(), new Intent(), this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            CHECK_POLICY();
        STRICT_POLICY();
        if (!CHECKED())
            LOGIN();
        else if (FirebaseAuth.getInstance().getUid() == null)
            LOGIN();
        else if (FirebaseAuth.getInstance().getUid() != null && CHECKED()) {
            bundle.putString("UI_to_display", "2");
            decide = new utils().bottom_nav(bottomNav, this,ui_loader);
            new utils().quick_commission_call(TAG);
            new utils().openFragment(new home(), this, bundle);
        }


    }


    public void NOTIFICATION_LISTER(Keep_alive keep_alive, Intent intent, AppCompatActivity application) {

        keep_alive = new Keep_alive();
        intent = new Intent(application, keep_alive.getClass());
        if (!isServicerunning(keep_alive.getClass(),application))
            application.startService(intent);

        if (!Pushy.isRegistered(application))
            new RegisterUser(application).execute();
        Pushy.listen(application);
    }


    private boolean isServicerunning(Class<? extends Keep_alive> aClass, AppCompatActivity application) {

        ActivityManager manager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(serviceInfo.service.getClassName())) {
                Log.d(TAG, " Service Already Running");
                return true;
            }
            Log.d(TAG, " Service Not Running");
        }
        return false;
    }


    //----------------------------------------------Permission for file sharing ---------------------------------------------//
    //Step 1
    public void STRICT_POLICY() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }


    //Step 2
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CHECK_POLICY() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else
            request_permission();
    }


    //Step 3
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This Permission is needed for file sharing")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);

        }
    }


    //Step 4
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                new utils().message2("Permission Granted", this);
            else
                new utils().message2("Permission Denied", this);

        }

    }
    //----------------------------------------------End of file sharing ---------------------------------------------//




    //----------------------------------------------onBackPressed ---------------------------------------------//
    @Override
    public void onBackPressed() {
        if (decide) {
            new utils().message1("Press twice to exit", getApplicationContext());
            if (back_pressed + Time_lapsed > System.currentTimeMillis()) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                free_memory();
            }
            back_pressed = System.currentTimeMillis();

        } else
            super.onBackPressed();

    }


    public void free_memory() {
        FragmentManager fm = getSupportFragmentManager();
        for (int x = 0; x < fm.getBackStackEntryCount(); ++x) {
            fm.popBackStack();
        }
    }
    //----------------------------------------------onBackPressed ---------------------------------------------//


    private boolean CHECKED() {
        if (new utils().init(getApplicationContext()).getString(getString(R.string.VENDOR), null) != null)
            return true;
        else
            return false;
    }

    private void LOGIN() {
        startActivity(new Intent(this, Login.class).putExtra("check_view", String.valueOf(2)));
    }

    public void space(View view) {
    }



}