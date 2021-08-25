package com.example.chauvendor.UI;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chauvendor.R;
import com.example.chauvendor.Running_Service.RegisterUser;
import com.example.chauvendor.model.UserLocation;
import com.example.chauvendor.util.utils;
import com.example.chauvendor.widget.ViewHeightAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import me.pushy.sdk.Pushy;

import static com.example.chauvendor.constant.Constants.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private BottomNavigationView bottomNav;
    private FirebaseFirestore mfirestore;
    private UserLocation muserLocation;
    private Bundle bundle;


    private String TAG = "MainActivity";
    private static long back_pressed;
    private static int Time_lapsed = 2000;
    private boolean decide = false;
    private final boolean mLocationPermissionGranted = false, decision = false;


    @Override
    protected void onResume() {
        super.onResume();
        decide = true;
        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
            new utils().message1(new utils().Stringnify(bundle.get("ID")), getApplicationContext());
            bundle.putString("ID",bundle.get("ID").toString());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottomNav);
        NOTIFICATION_LISTER();
        decide = new utils().bottom_nav(bottomNav,this,bundle);
        policy();
        check();
        if (FirebaseAuth.getInstance().getUid() != null) {
            mfirestore = FirebaseFirestore.getInstance();
            if(getIntent().getExtras()!=null)
                new utils().openFragment(new notification(),this,bundle);
            else
               new utils().openFragment(new home(),this,bundle);
        }

    }



    private void NOTIFICATION_LISTER() {
        if(!Pushy.isRegistered(getApplicationContext()))
            new RegisterUser(this).execute();
        Pushy.listen(this);
    }












    //----------------------------------------------Permission for   file sharing ---------------------------------------------//
    //Step 1
    public void policy() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.d(TAG, " Called !");
        }
    }


    //Step 2
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void check() {
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
            }

            back_pressed = System.currentTimeMillis();
            free_memory();
        } else
            super.onBackPressed();
    }


    public void free_memory() {
        FragmentManager fm = getSupportFragmentManager();
        for (int x = 0; x < fm.getBackStackEntryCount(); ++x) {
            fm.popBackStack();
        }
    }


    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.signuser, popup.getMenu());
        popup.show();
    }


    public void signin(MenuItem item) {
        if (FirebaseAuth.getInstance().getUid() == null)
            startActivity(new Intent(this, Login.class).putExtra("check_view", String.valueOf(2)));
        else
            new utils().message2("Pls Sign out ", this);
    }

    public void signout(MenuItem item) {
        if (FirebaseAuth.getInstance().getUid() != null) {
            new utils().message2(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail() + " Signed Out", this);
            FirebaseAuth.getInstance().signOut();
        } else
            new utils().message2("Already Signed Out", this);

    }


    public void search(MenuItem item) {
        decide = false;
        bundle.putString("ID","S");
       new utils().openFragment(new Search(),this,bundle);
    }

    public void space(View view) {
    }
}